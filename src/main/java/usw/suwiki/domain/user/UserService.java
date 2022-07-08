package usw.suwiki.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usw.suwiki.domain.email.ConfirmationToken;
import usw.suwiki.domain.email.ConfirmationTokenRepository;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.exam.ExamPosts;
import usw.suwiki.domain.refreshToken.RefreshTokenRepository;
import usw.suwiki.domain.reportTarget.EvaluatePostReport;
import usw.suwiki.domain.reportTarget.ExamPostReport;
import usw.suwiki.domain.userIsolation.UserIsolation;
import usw.suwiki.domain.email.EmailSender;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.domain.evaluation.JpaEvaluatePostsRepository;
import usw.suwiki.domain.exam.JpaExamPostsRepository;
import usw.suwiki.domain.reportTarget.EvaluateReportRepository;
import usw.suwiki.domain.reportTarget.ExamReportRepository;
import usw.suwiki.domain.userIsolation.UserIsolationRepository;
import usw.suwiki.domain.blacklistDomain.BlackListService;
import usw.suwiki.domain.emailBuild.BuildEmailAuthFormService;
import usw.suwiki.domain.emailBuild.BuildFindLoginIdFormService;
import usw.suwiki.domain.emailBuild.BuildFindPasswordFormService;
import usw.suwiki.domain.emailBuild.BuildSoonDormantTargetFormService;
import usw.suwiki.domain.email.ConfirmationTokenService;
import usw.suwiki.domain.evaluation.EvaluatePostsService;
import usw.suwiki.domain.exam.ExamPostsService;
import usw.suwiki.domain.viewExam.ViewExamService;
import usw.suwiki.global.jwt.JwtTokenValidator;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    // Another Service
    private final BlackListService blackListService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final ViewExamService viewExamService;

    // 암호화 Bean
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    // User 관련 Repository
    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    // 게시글 관련 Repository
    private final JpaEvaluatePostsRepository jpaEvaluatePostsRepository;
    private final JpaExamPostsRepository jpaExamPostsRepository;
    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;

    // Email
    private final EmailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;
    private final BuildEmailAuthFormService buildEmailAuthFormService;
    private final BuildFindLoginIdFormService BuildFindLoginIdFormService;
    private final BuildFindPasswordFormService BuildFindPasswordFormService;
    private final BuildSoonDormantTargetFormService buildSoonDormantTargetFormService;

    // JWT
    private final JwtTokenResolver jwtTokenResolver;

    //유저 저장
    @Transactional
    private User makeUser(UserDto.JoinForm joinForm) {
        User user = User.builder()
                .loginId((joinForm.getLoginId()))
                .password(bCryptPasswordEncoder.encode(joinForm.getPassword()))
                .email(joinForm.getEmail())
                .restricted(true)
                .bannedCount(0)
                .writtenEvaluation(0)
                .writtenExam(0)
                .point(0)
                .viewExamCount(0)
                .build();
        userRepository.save(user);
        return user;
    }

    //회원가입 처리(아이디,이메일 검증 -> 이메일 인증 토큰 메일로 발행)
    @Transactional
    public void join(UserDto.JoinForm joinForm) {

        //최종제출 폼에서 아이디와 이메일이 중복되지 않고
        if (userRepository.findByLoginId(joinForm.getLoginId()).isPresent() ||
                userRepository.findByEmail(joinForm.getEmail()).isPresent())
            throw new AccountException(ErrorType.USER_AND_EMAIL_OVERLAP);

        //학교 이메일 형식이 맞지 않으면 에러
        if (!joinForm.getEmail().contains("@suwon.ac.kr")) throw new AccountException(ErrorType.IS_NOT_EMAIL_FORM);

        //유저 데이터 임시 저장
        User user = makeUser(joinForm);

        //이메일 토큰 발행
        String token = UUID.randomUUID().toString();

        //이메일 토큰 부가 정보 생성
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        //이메일 토큰 저장
        confirmationTokenService.saveConfirmationToken(confirmationToken);

//        이메일 토큰에 대한 링크 생성
        String link = "https://api.suwiki.kr/user/verify-email/?token=" + token;
//        String link = "http://localhost:8080/user/verify-email/?token=" + token;

        //이메일 전송
        emailSender.send(joinForm.getEmail(), buildEmailAuthFormService.buildEmail(link));
    }

    //이메일 인증 토큰 만료 검사
    @Transactional
    public boolean isEmailAuthTokenExpired(ConfirmationToken confirmationToken) {

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        //만료 됐으면 true, 아니면 false
        return expiredAt.isBefore(LocalDateTime.now());
    }

    // 이메일 인증을 받은 사용자인지
    @Transactional
    public void isUserEmailAuth(String loginId) {
        User targetUser = loadUserFromLoginId(loginId);

        confirmationTokenRepository.verifyUserEmailAuth(targetUser.getId())
                .orElseThrow(() -> new AccountException(ErrorType.USER_NOT_EMAIL_AUTHED));
    }

    //아이디 찾기 메일 발송
    @Transactional
    public boolean sendEmailFindId(UserDto.FindIdForm findIdForm) {
        Optional<User> inquiryId = userRepository.findByEmail(findIdForm.getEmail());

        if (inquiryId.isPresent()) {
            emailSender.send(findIdForm.getEmail(), BuildFindLoginIdFormService.buildEmail(inquiryId.get().getLoginId()));
            return true;
        }
        throw new AccountException(ErrorType.USER_NOT_FOUND);
    }

    //임시 비밀번호 생성
    @Transactional
    public String randomizePassword() {
        StringBuilder sb = new StringBuilder();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        char[] charAllSet = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '!', '@', '#', '$', '%', '^'};

        char[] charNumberSet = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        char[] charSpecialSet = new char[]{ '!', '@', '#', '$', '%', '^' };

        int idx = 0;
        int allLen = charAllSet.length;
        int numberLen = charNumberSet.length;
        int specialLen = charSpecialSet.length;

        // 숫자 최소 1개를 포함하기 위한 반복문
        for (int i = 0; i < 1; i++) {
            idx = sr.nextInt(numberLen);
            sb.append(charNumberSet[idx]);
        }

        // 특수문자 최소 1개를 포함하기 위한 반복문
        for (int i = 0; i < 1; i++) {
            idx = sr.nextInt(specialLen);
            sb.append(charSpecialSet[idx]);
        }

        for (int i = 0; i < 6; i++) {
            idx = sr.nextInt(allLen);
            sb.append(charAllSet[idx]);
        }
        return sb.toString();
    }

    //임시 비밀번호 설정 후 메일 발송
    @Transactional
    public boolean sendEmailFindPassword(UserDto.FindPasswordForm findPasswordForm) {

        //아이디 이메일 체크
        if (userRepository.findPwLogicByLoginIdAndEmail(findPasswordForm.getLoginId(), findPasswordForm.getEmail()) != null) {

            //임시 비밀번호 발급
            String resetPassword = randomizePassword();

            //DB에 암호화
            String EncodedResetPassword = bCryptPasswordEncoder.encode(resetPassword);

            //암호화 한 비밀번호 저장
            userRepository.resetPassword(EncodedResetPassword, findPasswordForm.getLoginId(), findPasswordForm.getEmail());

            //이메일 발송
            emailSender.send(findPasswordForm.getEmail(), BuildFindPasswordFormService.buildEmail(resetPassword));

            return true;
        }
        throw new AccountException(ErrorType.USER_NOT_FOUND);
    }

    //마이페이지에서 비밀번호 재설정
    @Transactional
    public void editMyPassword(UserDto.EditMyPasswordForm editMyPasswordForm, String AccessToken) {

        //액세스 토큰 파싱 후 user 인덱스 확인
        String userLoginId = jwtTokenResolver.getLoginId(AccessToken);

        //재설정한 비밀번호 받아서 암호화 후 저장
        userRepository.editPassword(bCryptPasswordEncoder.encode(editMyPasswordForm.getNewPassword()), userLoginId);

        //UpdatedAt 타임스탬프
        User user = loadUserFromLoginId(userLoginId);

        user.setUpdatedAt(LocalDateTime.now());

    }

    //유저 제재 여부 확인
    @Transactional
    public void isRestricted(String loginId) {
        if (userRepository.loadUserRestriction(loginId)) throw new AccountException(ErrorType.USER_RESTRICTED);
    }

    @Transactional
    public void restrictedUser(Long userIdx) {
        userRepository.restrictUser(userIdx);
    }

    //비밀번호 검증 True 시 비밀번호 일치
    @Transactional
    public boolean correctPw(String loginId, String password) {

        //로그인 아이디를 찾을 수 없으면
        if (userRepository.findByLoginId(loginId).isEmpty()) throw new AccountException(ErrorType.USER_NOT_EXISTS);

        return bCryptPasswordEncoder.matches(password, userRepository.findByLoginId(loginId).get().getPassword());
    }

    //아이디 비밀번호 매칭
    @Transactional
    public boolean matchingLoginIdWithPassword(String loginId, String password) {

        //아이디 조회
        userRepository.findByLoginId(loginId).orElseThrow(() -> new AccountException(ErrorType.USER_NOT_EXISTS));

        //비밀번호 일치하지 않으면
        if (!correctPw(loginId, password)) throw new AccountException(ErrorType.PASSWORD_ERROR);

        return true;
    }

    //최근 로그인 일자 갱신
    @Transactional
    public void setLastLogin(UserDto.LoginForm loginForm) {
        Optional<User> user = userRepository.findByLoginId(loginForm.getLoginId());
        user.ifPresent(value -> value.setLastLogin(LocalDateTime.now()));
    }

    //Optional<User> -> User
    @Transactional
    public User convertOptionalUserToDomainUser(Optional<User> optionalUser) {
        if (optionalUser.isPresent()) return optionalUser.get();

        throw new AccountException(ErrorType.USER_NOT_EXISTS);
    }

    //userIdx 로 유저 꺼내오기
    @Transactional
    public User loadUserFromUserIdx(Long userIdx) {
        return convertOptionalUserToDomainUser(userRepository.findById(userIdx));
    }

    //loginId로 유저 테이블 꺼내오기
    @Transactional
    public User loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userRepository.findByLoginId(loginId));
    }

    //회원탈퇴 요청 시각 스탬프
    @Transactional
    public void requestQuitDateStamp(User user) {
        user.setRequestedQuitDate(LocalDateTime.now());
    }

    // 회원탈퇴 요청 시각 스탬프 초기화
    @Transactional
    public void initQuitDateStamp(User user) {
        user.setRequestedQuitDate(null);
    }

    // 본 테이블 -> 격리 테이블
    @Transactional
    public void moveToIsolation(User user) {
        // 격리 테이블로 옮기기
        userIsolationRepository.insertUserIntoIsolation(user.getId());
    }

    // 격리 테이블 -> 본 테이블
    @Transactional
    public void moveToUser(UserIsolation userIsolation) {
        // 유저 테이블로 옮기기
        userRepository.insertUserIsolationIntoUser(userIsolation.getId());

        // 유저테이블로 옮겼으면 격리 테이블에서 삭제하기
        userIsolationRepository.deleteByLoginId(userIsolation.getLoginId());
    }


    // 휴면계정 전환 30일 전 대상 뽑기
    // 검사하는 시점 보다 최근 로그인 일자가 30일 이전인 유저를 리스트에 담는다.
    @Transactional
    public List<User> soonDormant() {
//        LocalDateTime targetTime = LocalDateTime.now().minusMonths(11);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(3);
        return userRepository.findByLastLoginBefore(targetTime);
    }

    //휴면 계정 전환 30일 전 안내 메일 보내기
    @Transactional
    @Scheduled(cron = "0 * * * * *") //매일 0시에 한번 씩 돌린다.
    public void sendEmailSoonDormant() {

        //대상 유저 가져오기
        List<User> user = soonDormant();

        //대상 유저들에 이메일 보내기
        for (int i = 0; i < user.toArray().length; i++) {
            emailSender.send(user.get(i).getEmail(), buildSoonDormantTargetFormService.buildEmail());
        }
    }

    //휴면계정 전환
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void convertDormant() {

//        LocalDateTime targetTime = LocalDateTime.now().minusMonths(12);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(3);

        //1년이상 접속하지 않은 유저 리스트 불러오기
        List<User> targetUser = userRepository.findByLastLoginBefore(targetTime);

        //해당 유저들 격리테이블로 이동 후, 본 테이블에서 삭제
        for (int i = 0; i < targetUser.toArray().length; i++) {
            moveToIsolation(targetUser.get(i));
            userRepository.deleteById(targetUser.get(i).getId());
        }
    }


    //회원탈퇴 대기
    @Transactional
    public void waitQuit(Long userIdx) {

        //구매한 시험 정보 삭제
        viewExamService.deleteByUserIdx(userIdx);

        //회원탈퇴 요청한 유저의 강의평가 삭제
        evaluatePostsService.deleteByUser(userIdx);

        //회원탈퇴 요청한 유저의 시험정보 삭제
        examPostsService.deleteByUser(userIdx);

        //유저 이용불가 처리
        disableUser(loadUserFromUserIdx(userIdx));
    }

    //회원탈퇴 요청 유저 일부 데이터 초기화
    @Transactional
    public void disableUser(User user) {
        user.setRestricted(true);
        user.setBannedCount(null);
        user.setRole(null);
        user.setWrittenEvaluation(null);
        user.setWrittenExam(null);
        user.setViewExamCount(null);
        user.setPoint(null);
        user.setLastLogin(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);
    }

    //회원탈퇴 요청 후 30일 뒤 테이블에서 제거
    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 * * * * *")
    public void deleteRequestQuitUserAfter30Days() {

//        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(3);

        List<User> targetUser = userRepository.findByRequestedQuitDate(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {
            userIsolationRepository.deleteByLoginId(targetUser.get(i).getLoginId());
        }
    }

    // 이용정지를 풀기 위한 메서드
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void isUnrestrictedTarget() {

        // 현재시각으로부터 - 30일
        LocalDateTime targetTime = LocalDateTime.now();
        
        List<UserIsolation> targetUser = userIsolationRepository.findByRestrictingDateBefore(targetTime);
    }


    // 강의평가 인덱스로 강의평가 객체 불러오기
    @Transactional
    public EvaluatePosts loadEvaluatePostsByIndex(Long EvaluatePostsIdx) {

        return jpaEvaluatePostsRepository.findById(EvaluatePostsIdx);
    }

    @Transactional
    public ExamPosts loadExamPostsByIndex(Long ExamPostsIdx) {
        return jpaExamPostsRepository.findById(ExamPostsIdx);
    }

    //신고 받은 대상 신고 테이블에 저장
    @Transactional
    public void reportExamPost(UserDto.ExamReportForm userReportForm, Long reportingUserIdx) {

        Long reportTargetUser = loadExamPostsByIndex(userReportForm.getExamIdx()).getUser().getId();

        ExamPosts reportedTargetPost = loadExamPostsByIndex(userReportForm.getExamIdx());

        ExamPostReport target = ExamPostReport.builder()
                .examIdx(userReportForm.getExamIdx())
                .lectureName(reportedTargetPost.getLectureName())
                .professor(reportedTargetPost.getProfessor())
                .content(reportedTargetPost.getContent())
                .reportedUserIdx(reportTargetUser)
                .reportingUserIdx(reportingUserIdx)
                .reportedDate(LocalDateTime.now())
                .build();

        examReportRepository.save(target);
    }

    //신고 받은 대상 신고 테이블에 저장
    @Transactional
    public void reportEvaluatePost(UserDto.EvaluateReportForm userReportForm, Long reportingUserIdx) {

        Long reportTargetUser = loadEvaluatePostsByIndex(userReportForm.getEvaluateIdx()).getUser().getId();

        EvaluatePosts reportTargetPost = loadEvaluatePostsByIndex(userReportForm.getEvaluateIdx());

        EvaluatePostReport target = EvaluatePostReport.builder()
                .evaluateIdx(userReportForm.getEvaluateIdx())
                .lectureName(reportTargetPost.getLectureName())
                .professor(reportTargetPost.getProfessor())
                .content(reportTargetPost.getContent())
                .reportedUserIdx(reportTargetUser)
                .reportingUserIdx(reportingUserIdx)
                .reportedDate(LocalDateTime.now())
                .build();

        evaluateReportRepository.save(target);
    }
}