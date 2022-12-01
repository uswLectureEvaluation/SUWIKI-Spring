package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.entity.ConfirmationToken;
import usw.suwiki.domain.email.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.email.service.ConfirmationTokenService;
import usw.suwiki.domain.email.service.EmailSender;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.repository.EvaluatePostsRepository;
import usw.suwiki.domain.exam.entity.ExamPosts;
import usw.suwiki.domain.exam.repository.ExamPostsRepository;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.user.dto.UserDto;
import usw.suwiki.domain.user.dto.UserDto.EditMyPasswordForm;
import usw.suwiki.domain.user.dto.UserDto.FindPasswordForm;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthFormService;
import usw.suwiki.global.util.emailBuild.BuildFindLoginIdFormService;
import usw.suwiki.global.util.emailBuild.BuildFindPasswordFormService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static usw.suwiki.exception.ErrorType.*;


@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EvaluatePostsRepository evaluatePostsRepository;
    private final ExamPostsRepository examPostsRepository;
    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;
    private final EmailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;
    private final BuildEmailAuthFormService buildEmailAuthFormService;
    private final BuildFindLoginIdFormService BuildFindLoginIdFormService;
    private final BuildFindPasswordFormService BuildFindPasswordFormService;
    private final JwtTokenResolver jwtTokenResolver;

    @Transactional
    public User makeUser(UserDto.JoinForm joinForm) {
        User user = User.builder()
                .loginId((joinForm.getLoginId()))
                .password(bCryptPasswordEncoder.encode(joinForm.getPassword()))
                .email(joinForm.getEmail())
                .restricted(true)
                .restrictedCount(0)
                .writtenEvaluation(0)
                .writtenExam(0)
                .point(0)
                .viewExamCount(0)
                .build();
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void join(UserDto.JoinForm joinForm) {
        if (userRepository.findByLoginId(joinForm.getLoginId()).isPresent() ||
                userRepository.findByEmail(joinForm.getEmail()).isPresent())
            throw new AccountException(ErrorType.USER_AND_EMAIL_OVERLAP);

        if (!joinForm.getEmail().contains("@suwon.ac.kr")) throw new AccountException(ErrorType.IS_NOT_EMAIL_FORM);

        User user = makeUser(joinForm);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .userIdx(user.getId())
                .build();
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String link = "https://api.suwiki.kr/user/verify-email/?token=" + token;
//        String link = "http://localhost:8080/user/verify-email/?token=" + token;
        emailSender.send(joinForm.getEmail(), buildEmailAuthFormService.buildEmail(link));
    }

    @Transactional
    public boolean isEmailAuthTokenExpired(ConfirmationToken confirmationToken) {
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        return expiredAt.isBefore(LocalDateTime.now());
    }

    // 이메일 인증을 받은 사용자인지 유저 테이블에서 검사
    @Transactional
    public void isUserEmailAuth(Long userIdx) {
        User targetUser = loadUserFromUserIdx(userIdx);
        confirmationTokenRepository.verifyUserEmailAuth(targetUser.getId())
                .orElseThrow(() -> new AccountException(USER_NOT_EMAIL_AUTHED));
    }

    //아이디 찾기 메일 발송
    @Transactional
    public boolean sendEmailFindId(UserDto.FindIdForm findIdForm) {
        Optional<User> inquiryId = userRepository.findByEmail(findIdForm.getEmail());

        if (inquiryId.isPresent()) {
            emailSender.send(findIdForm.getEmail(), BuildFindLoginIdFormService.buildEmail(inquiryId.get().getLoginId()));
            return true;
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    @Transactional
    public String randomizePassword() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(new Date().getTime());

        char[] charAllSet = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '!', '@', '#', '$', '%', '^'};
        char[] charNumberSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] charSpecialSet = new char[]{'!', '@', '#', '$', '%', '^'};
        int idx = 0;
        int allLen = charAllSet.length;
        int numberLen = charNumberSet.length;
        int specialLen = charSpecialSet.length;

        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < 1; i++) {
            idx = secureRandom.nextInt(numberLen);
            newPassword.append(charNumberSet[idx]);
        }

        for (int i = 0; i < 1; i++) {
            idx = secureRandom.nextInt(specialLen);
            newPassword.append(charSpecialSet[idx]);
        }

        for (int i = 0; i < 6; i++) {
            idx = secureRandom.nextInt(allLen);
            newPassword.append(charAllSet[idx]);
        }
        return newPassword.toString();
    }

    public boolean sendEmailFindPassword(FindPasswordForm findPasswordForm) {
        if (userRepository.findPwLogicByLoginIdAndEmail(findPasswordForm.getLoginId(), findPasswordForm.getEmail()) != null) {
            String resetPassword = randomizePassword();
            String EncodedResetPassword = bCryptPasswordEncoder.encode(resetPassword);
            userRepository.resetPassword(EncodedResetPassword, findPasswordForm.getLoginId(), findPasswordForm.getEmail());
            emailSender.send(findPasswordForm.getEmail(), BuildFindPasswordFormService.buildEmail(resetPassword));
            return true;
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    public void editMyPassword(EditMyPasswordForm editMyPasswordForm, String AccessToken) {
        String userLoginId = jwtTokenResolver.getLoginId(AccessToken);
        userRepository.editPassword(bCryptPasswordEncoder.encode(editMyPasswordForm.getNewPassword()), userLoginId);
        User user = loadUserFromLoginId(userLoginId);
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void validatePasswordAtEditPassword(String loginId, String prePassword) {
        if (userRepository.findByLoginId(loginId).isEmpty()) throw new AccountException(USER_NOT_EXISTS);

        if (bCryptPasswordEncoder.matches(prePassword, userRepository.findByLoginId(loginId).get().getPassword())) {
            bCryptPasswordEncoder.matches(prePassword, userRepository.findByLoginId(loginId).get().getPassword());
            return;
        }

        throw new AccountException(PASSWORD_ERROR);
    }

    @Transactional
    public void compareNewPasswordVersusPrePassword(String loginId, String newPassword) {

        if (bCryptPasswordEncoder.matches(newPassword, userRepository.findByLoginId(loginId).get().getPassword())) {
            throw new AccountException(PASSWORD_NOT_CHANGED);
        }
    }

    @Transactional
    public boolean validatePasswordAtUserTable(String loginId, String password) {
        return bCryptPasswordEncoder.matches(password, userRepository.findByLoginId(loginId).get().getPassword());
    }

    @Transactional
    public void setLastLogin(User user) {
        userRepository.lastLoginStamp(LocalDateTime.now(), user.getId());
    }

    @Transactional
    public User convertOptionalUserToDomainUser(Optional<User> optionalUser) {
        if (optionalUser.isPresent()) return optionalUser.get();
        throw new AccountException(USER_NOT_EXISTS);
    }

    @Transactional
    public User loadUserFromUserIdx(Long userIdx) {
        return convertOptionalUserToDomainUser(userRepository.findById(userIdx));
    }

    @Transactional
    public User loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userRepository.findByLoginId(loginId));
    }

    @Transactional
    public EvaluatePosts loadEvaluatePostsByIndex(Long EvaluatePostsIdx) {
        return evaluatePostsRepository.findById(EvaluatePostsIdx);
    }

    @Transactional
    public ExamPosts loadExamPostsByIndex(Long ExamPostsIdx) {
        return examPostsRepository.findById(ExamPostsIdx);
    }

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

    @Transactional
    public Long whoIsEvaluateReporting(Long evaluateIdx) {
        return evaluateReportRepository.findByEvaluateIdx(evaluateIdx).get().getReportingUserIdx();
    }

    @Transactional
    public Long whoIsExamReporting(Long examIdx) {
        return examReportRepository.findByExamIdx(examIdx).get().getReportingUserIdx();
    }
}