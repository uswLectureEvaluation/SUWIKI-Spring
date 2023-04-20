package usw.suwiki.domain.user.service;

import static usw.suwiki.global.exception.ErrorType.IS_NOT_EMAIL_FORM;
import static usw.suwiki.global.exception.ErrorType.PASSWORD_ERROR;
import static usw.suwiki.global.exception.ErrorType.PASSWORD_NOT_CHANGED;
import static usw.suwiki.global.exception.ErrorType.USER_AND_EMAIL_OVERLAP;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_EMAIL_AUTHED;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_EXISTS;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_FOUND;
import static usw.suwiki.global.util.ApiResponseFactory.successFlag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import usw.suwiki.domain.user.dto.UserRequestDto.FindIdForm;
import usw.suwiki.domain.user.dto.UserRequestDto.FindPasswordForm;
import usw.suwiki.domain.user.dto.UserRequestDto.JoinForm;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthForm;
import usw.suwiki.global.util.emailBuild.BuildFindLoginIdForm;
import usw.suwiki.global.util.emailBuild.BuildFindPasswordForm;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final String BASE_LINK = "https://api.suwiki.kr/user/verify-email/?token=";
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;
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

    public Map<String, Boolean> executeCheckId(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent() ||
            userIsolationRepository.findByLoginId(loginId).isPresent()) {
            return new HashMap<>() {{
                put("overlap", true);
            }};
        }
        return new HashMap<>() {{
            put("overlap", false);
        }};
    }

    public void join(JoinForm joinForm) {
        if (userRepository.findByLoginId(joinForm.getLoginId()).isPresent() ||
            userRepository.findByEmail(joinForm.getEmail()).isPresent()) {
            throw new AccountException(USER_AND_EMAIL_OVERLAP);
        }

        if (!joinForm.getEmail().contains("@suwon.ac.kr")) {
            throw new AccountException(IS_NOT_EMAIL_FORM);
        }

        User user = User.makeUser(
            joinForm.getLoginId(),
            bCryptPasswordEncoder.encode(joinForm.getPassword()),
            joinForm.getEmail()
        );

        ConfirmationToken confirmationToken = ConfirmationToken.makeToken(user);

        confirmationTokenService.saveConfirmationToken(ConfirmationToken.makeToken(user));
        emailSender.send(
            joinForm.getEmail(),
            buildEmailAuthForm
                .buildEmail(BASE_LINK + confirmationToken.getToken())
        );
    }

    // 이메일 인증을 받은 사용자인지 유저 테이블에서 검사
    public void isUserEmailAuth(Long userIdx) {
        Optional<ConfirmationToken> confirmationToken =
            confirmationTokenRepository.findByUserIdx(userIdx);
        if (confirmationToken.isPresent()) {
            confirmationToken.get().isVerified();
            return;
        }
        throw new AccountException(USER_NOT_EMAIL_AUTHED);
    }

    public boolean sendEmailFindId(FindIdForm findIdForm) {
        Optional<User> requestUser = userRepository.findByEmail(findIdForm.getEmail());

        if (requestUser.isPresent()) {
            emailSender.send(findIdForm.getEmail(),
                BuildFindLoginIdForm.buildEmail(requestUser.get().getLoginId()));
            return true;
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    public boolean sendEmailFindPassword(FindPasswordForm findPasswordForm) {
        Optional<User> user = userRepository.findByLoginId(findPasswordForm.getLoginId());
        if (user.isPresent()) {
            emailSender.send(findPasswordForm.getEmail(), BuildFindPasswordForm.buildEmail(
                user.get().updateRandomPassword(
                    bCryptPasswordEncoder)
            ));
            return true;
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    public Map<String, Boolean> executeEditPassword(
        User user, String prePassword, String newPassword) {
        if (prePassword.equals(newPassword)) {
            throw new AccountException(PASSWORD_NOT_CHANGED);
        } else if (!user.getPassword().equals(bCryptPasswordEncoder.encode(prePassword))) {
            throw new AccountException(PASSWORD_ERROR);
        }
        user.updatePassword(bCryptPasswordEncoder, newPassword);
        return successFlag();
    }

    public boolean validatePasswordAtUserTable(String loginId, String password) {
        return bCryptPasswordEncoder.matches(password,
            userRepository.findByLoginId(loginId).get().getPassword());
    }

    public void setLastLogin(User user) {
        userRepository.updateLastLogin(LocalDateTime.now(), user.getId());
    }

    public User convertOptionalUserToDomainUser(Optional<User> optionalUser) {
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
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

    public void reportExamPost(UserRequestDto.ExamReportForm userReportForm,
        Long reportingUserIdx) {
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

    public void reportEvaluatePost(UserRequestDto.EvaluateReportForm userReportForm,
        Long reportingUserIdx) {
        Long reportTargetUser = loadEvaluatePostsByIndex(userReportForm.getEvaluateIdx()).getUser()
            .getId();
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