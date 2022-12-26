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
import usw.suwiki.domain.user.dto.UserRequestDto;
import usw.suwiki.domain.user.dto.UserRequestDto.EditMyPasswordForm;
import usw.suwiki.domain.user.dto.UserRequestDto.FindIdForm;
import usw.suwiki.domain.user.dto.UserRequestDto.FindPasswordForm;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.global.exception.ErrorType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthForm;
import usw.suwiki.global.util.emailBuild.BuildFindLoginIdForm;
import usw.suwiki.global.util.emailBuild.BuildFindPasswordForm;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static usw.suwiki.global.exception.ErrorType.*;


@Service
@RequiredArgsConstructor
@Transactional
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
    private final BuildEmailAuthForm buildEmailAuthForm;
    private final BuildFindLoginIdForm BuildFindLoginIdForm;
    private final BuildFindPasswordForm BuildFindPasswordForm;
    private final JwtTokenResolver jwtTokenResolver;

    public User makeUser(UserRequestDto.JoinForm joinForm) {
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

    public void join(UserRequestDto.JoinForm joinForm) {
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
        emailSender.send(joinForm.getEmail(), buildEmailAuthForm.buildEmail(link));
    }

    public boolean isEmailAuthTokenExpired(ConfirmationToken confirmationToken) {
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        return expiredAt.isBefore(LocalDateTime.now());
    }

    // 이메일 인증을 받은 사용자인지 유저 테이블에서 검사
    public void isUserEmailAuth(Long userIdx) {
        User targetUser = loadUserFromUserIdx(userIdx);
        confirmationTokenRepository.verifyUserEmailAuth(targetUser.getId())
                .orElseThrow(() -> new AccountException(USER_NOT_EMAIL_AUTHED));
    }

    public boolean sendEmailFindId(FindIdForm findIdForm) {
        Optional<User> requestUser = userRepository.findByEmail(findIdForm.getEmail());

        if (requestUser.isPresent()) {
            emailSender.send(findIdForm.getEmail(), BuildFindLoginIdForm.buildEmail(requestUser.get().getLoginId()));
            return true;
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    public String randomizePassword() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(new Date().getTime());

        char[] charAllSet = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
                'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
                't', 'u', 'v', 'w', 'x', 'y', 'z',
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
            emailSender.send(findPasswordForm.getEmail(), BuildFindPasswordForm.buildEmail(resetPassword));
            return true;
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    public void editMyPassword(EditMyPasswordForm editMyPasswordForm, String AccessToken) {
        String userLoginId = jwtTokenResolver.getLoginId(AccessToken);
        userRepository.editPassword(bCryptPasswordEncoder.encode(editMyPasswordForm.getNewPassword()), userLoginId);
        User user = loadUserFromLoginId(userLoginId);
        userRepository.updateUpdatedAt(user.getId());
    }

    public void validatePasswordAtEditPassword(String loginId, String prePassword) {
        if (userRepository.findByLoginId(loginId).isEmpty()) throw new AccountException(USER_NOT_EXISTS);
        if (bCryptPasswordEncoder.matches(prePassword, userRepository.findByLoginId(loginId).get().getPassword())) {
            bCryptPasswordEncoder.matches(prePassword, userRepository.findByLoginId(loginId).get().getPassword());
            return;
        }
        throw new AccountException(PASSWORD_ERROR);
    }

    public void compareNewPasswordVersusPrePassword(String loginId, String newPassword) {
        if (bCryptPasswordEncoder.matches(newPassword, userRepository.findByLoginId(loginId).get().getPassword())) {
            throw new AccountException(PASSWORD_NOT_CHANGED);
        }
    }

    public boolean validatePasswordAtUserTable(String loginId, String password) {
        return bCryptPasswordEncoder.matches(password, userRepository.findByLoginId(loginId).get().getPassword());
    }

    public void setLastLogin(User user) {
        userRepository.lastLoginStamp(LocalDateTime.now(), user.getId());
    }

    public User convertOptionalUserToDomainUser(Optional<User> optionalUser) {
        if (optionalUser.isPresent()) return optionalUser.get();
        throw new AccountException(USER_NOT_EXISTS);
    }

    public User loadUserFromUserIdx(Long userIdx) {
        return convertOptionalUserToDomainUser(userRepository.findById(userIdx));
    }

    public User loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userRepository.findByLoginId(loginId));
    }

    public EvaluatePosts loadEvaluatePostsByIndex(Long EvaluatePostsIdx) {
        return evaluatePostsRepository.findById(EvaluatePostsIdx);
    }

    public ExamPosts loadExamPostsByIndex(Long ExamPostsIdx) {
        return examPostsRepository.findById(ExamPostsIdx);
    }

    public void reportExamPost(UserRequestDto.ExamReportForm userReportForm, Long reportingUserIdx) {
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

    public void reportEvaluatePost(UserRequestDto.EvaluateReportForm userReportForm, Long reportingUserIdx) {
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

    public Long whoIsEvaluateReporting(Long evaluateIdx) {
        return evaluateReportRepository.findByEvaluateIdx(evaluateIdx).get().getReportingUserIdx();
    }

    public Long whoIsExamReporting(Long examIdx) {
        return examReportRepository.findByExamIdx(examIdx).get().getReportingUserIdx();
    }
}