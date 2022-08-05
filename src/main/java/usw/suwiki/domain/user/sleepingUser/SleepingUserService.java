package usw.suwiki.domain.user.sleepingUser;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistDomain.BlackListService;
import usw.suwiki.domain.email.ConfirmationTokenRepository;
import usw.suwiki.domain.email.EmailSender;
import usw.suwiki.domain.emailBuild.BuildAutoDeletedWarningUserFormService;
import usw.suwiki.domain.emailBuild.BuildSoonDormantTargetFormService;
import usw.suwiki.domain.evaluation.EvaluatePostsService;
import usw.suwiki.domain.exam.ExamPostsService;
import usw.suwiki.domain.favorite_major.FavoriteMajorService;
import usw.suwiki.domain.reportTarget.EvaluateReportRepository;
import usw.suwiki.domain.reportTarget.ExamReportRepository;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserDto;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.UserService;
import usw.suwiki.domain.user.restrictingUser.RestrictingUserRepository;
import usw.suwiki.domain.user.restrictingUser.RestrictingUserService;
import usw.suwiki.domain.userIsolation.UserIsolation;
import usw.suwiki.domain.userIsolation.UserIsolationRepository;
import usw.suwiki.domain.userIsolation.UserIsolationService;
import usw.suwiki.domain.viewExam.ViewExamService;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SleepingUserService {

    // User
    private final UserService userService;
    private final UserRepository userRepository;
    private final FavoriteMajorService favoriteMajorService;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    // 휴면 계정
    private final UserIsolationService userIsolationService;
    private final UserIsolationRepository userIsolationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BuildSoonDormantTargetFormService buildSoonDormantTargetFormService;
    private final BuildAutoDeletedWarningUserFormService buildAutoDeletedWarningUserFormService;
    private final EmailSender emailSender;
    private final RestrictingUserRepository restrictingUserRepository;
    private final ExamReportRepository examReportRepository;
    private final EvaluateReportRepository evaluateReportRepository;

    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;

    private final ViewExamService viewExamService;


    // 본 테이블 -> 격리 테이블
    @Transactional
    public void moveToIsolation(User user) {

        // 격리 테이블로 유저 인덱스, 유저 로그인 아이디, 유저 비밀번호, 유저 이메일 옮기기
        userIsolationRepository.convertSleepingUser(user.getId());

        // 유저 테이블 개인정보 컬럼 null 값 처리.
        userRepository.convertToSleeping(user.getId());
    }

    // 휴면계정 아이디 비밀번호 매칭
    @Transactional
    public boolean validatePasswordAtIsolationTable(String loginId, String password) {
        return bCryptPasswordEncoder.matches(password, userIsolationRepository.findByLoginId(loginId).get().getPassword());
    }

    // 휴면계정 테이블에 있으면
    @Transactional
    public User sleepingUserLogin(UserDto.LoginForm loginForm) {

        // 로그인 아이디로 격리 테이블 객체로 뽑아오기
        UserIsolation userIsolation = userIsolationService.loadUserFromLoginId(loginForm.getLoginId());

        // 휴면계정 테이블에서 아이디 비밀번호 검증
        if (validatePasswordAtIsolationTable(loginForm.getLoginId(), loginForm.getPassword())) {
            // 휴면 계정 테이블(userIdx, loginId, password, email) -> 유저 테이블 (id, loginId, password, email)
            userRepository.convertToWakeUp(userIsolation.getUserIdx());

            // 휴면 계정 테이블에서 삭제
            userIsolationRepository.deleteByLoginId(loginForm.getLoginId());
        } else {
            throw new AccountException(ErrorType.PASSWORD_ERROR);
        }
        return userService.loadUserFromLoginId(loginForm.getLoginId());
    }
    
    /**
    배포환경 : 휴면 계정 전환 30일 전(마지막 로그인 일자가 11달 전) 안내 메일 보내기
     **/

    @Transactional
    @Scheduled(cron = "2 * * * * *")
    public void sendEmailSoonDormant() {

        // 마지막 로그인 일자가 지금으로부터 11개월 전인 유저에게
//        LocalDateTime targetTime = LocalDateTime.now().minusMonths(11);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(1);

        // 11개월 접속안한 유저 목록 가져오기
        List<User> user = userRepository.findByLastLoginBefore(targetTime);

        // 대상 유저들에 이메일 보내기
        for (int i = 0; i < user.toArray().length; i++) {
            if (user.get(i).getEmail() != null) {
                emailSender.send(user.get(i).getEmail(), buildSoonDormantTargetFormService.buildEmail());
            }
        }
    }

    /**
     배포환경 : 마지막 로그인 일자가 12달 전 유저 휴면계정처리

     테스트환경 : 마지막 로그인 일자가 10분 전인 유저 휴면계정처리

     **/
    @Transactional
    // @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "4 * * * * *")
    public void convertSleepingTable() {

//        LocalDateTime targetTime = LocalDateTime.now().minusMonths(12);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(2);

        // 1년이상 접속하지 않은 유저 리스트 불러오기
        List<User> targetUser = userRepository.findByLastLoginBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {
            // 해당 유저가 휴면계정에 없을 때만 휴면계정에 삽입
            if (userIsolationRepository.findByUserIdx(targetUser.get(i).getId()).isEmpty()) {
                moveToIsolation(targetUser.get(i));
            }
        }
    }

    // 휴면계정 전환 후 3년간 로그인 하지 않는 대상에 지정되기 한달 전에 이메일 보내기
    // 테스트 환경 -> 20분 미 접속 시 자동 삭제 대상 메일 전송
    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "6 * * * * *")
    public void autoDeleteTargetIsThreeYearsSendEmail() {
//        LocalDateTime endTime = LocalDateTime.now().minusYears(3).plusDays(30);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(5);
        List<UserIsolation> targetUser = userIsolationRepository.findByLastLoginBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {
            emailSender.send(targetUser.get(i).getEmail(), buildAutoDeletedWarningUserFormService.buildEmail());
        }
    }

    // 3년간 로그인 하지 않으면 계정 자동 삭제
    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "8 * * * * *")
    public void autoDeleteTargetIsThreeYears() {
//        LocalDateTime targetTime = LocalDateTime.now().minusYears(3);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(7);
        List<UserIsolation> targetUser = userIsolationRepository.findByLastLoginBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {

            // 삭제 예정 유저의 구매한 시험 정보 삭제
            viewExamService.deleteByUserIdx(targetUser.get(i).getUserIdx());

            // 신고된 시험정보 삭제
            examReportRepository.deleteByReportedUserIdx(targetUser.get(i).getUserIdx());
            examReportRepository.deleteByReportingUserIdx(targetUser.get(i).getUserIdx());

            // 신고된 강의평가 삭제
            evaluateReportRepository.deleteByReportingUserIdx(targetUser.get(i).getUserIdx());
            evaluateReportRepository.deleteByReportedUserIdx(targetUser.get(i).getUserIdx());

            // 삭제 예정 유저의 강의평가 삭제
            evaluatePostsService.deleteByUser(targetUser.get(i).getUserIdx());

            // 삭제 예정 유저의 시험정보 삭제
            examPostsService.deleteByUser(targetUser.get(i).getUserIdx());

            // 즐겨찾기 게시글 삭제
            favoriteMajorService.deleteAllByUser(targetUser.get(i).getUserIdx());

            // 삭제 예정 유저의 정지 테이블 삭제
            restrictingUserRepository.deleteByUserIdx(targetUser.get(i).getUserIdx());

            // 이메일 인증 토큰 삭제
            confirmationTokenRepository.deleteByUserIdx(targetUser.get(i).getUserIdx());

            // 휴면계정에서 유저 삭제
            userIsolationRepository.deleteByLoginId(targetUser.get(i).getLoginId());

            // 본 테이블에서 유저 삭제
            userRepository.deleteById(targetUser.get(i).getUserIdx());
        }
    }
}