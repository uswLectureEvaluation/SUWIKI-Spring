package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.secure.PasswordEncoder;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exampost.ExamPost;
import usw.suwiki.domain.exampost.service.ExamPostCRUDService;
import usw.suwiki.domain.user.User;
import usw.suwiki.report.ReportPostService;
import usw.suwiki.report.evaluatepost.EvaluatePostReport;
import usw.suwiki.report.exampost.ExamPostReport;

import java.util.List;
import java.util.Map;

import static usw.suwiki.common.response.ApiResponseFactory.adminLoginResponseForm;
import static usw.suwiki.common.response.ApiResponseFactory.successCapitalFlag;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostNoProblemForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostNoProblemForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostRestrictForm;
import static usw.suwiki.domain.user.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminBusinessService {
    private static final long BANNED_PERIOD = 365L;

    private final PasswordEncoder passwordEncoder;
    private final UserCRUDService userCRUDService;
    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final UserIsolationCRUDService userIsolationCRUDService;
    private final RestrictingUserService restrictingUserService;

    private final ReportPostService reportPostService;
    private final ExamPostCRUDService examPostCRUDService;
    private final EvaluatePostCRUDService evaluatePostCRUDService;

    private final JwtAgent jwtAgent;

    public Map<String, String> executeAdminLogin(LoginForm loginForm) {
        User user = userCRUDService.loadUserFromLoginId(loginForm.loginId());
        if (user.validatePassword(passwordEncoder, loginForm.password())) {
            if (user.isAdmin()) {
                final long userCount = userCRUDService.countAllUsers();
                final long userIsolationCount = userIsolationCRUDService.countAllIsolatedUsers();
                final long totalUserCount = userCount + userIsolationCount;

                return adminLoginResponseForm(jwtAgent.createAccessToken(user), String.valueOf(totalUserCount));
            }
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
        throw new AccountException(ExceptionType.PASSWORD_ERROR);
    }

    public LoadAllReportedPostForm executeLoadAllReportedPosts() {
        List<EvaluatePostReport> evaluatePostReports = reportPostService.loadAllEvaluateReports();
        List<ExamPostReport> examPostReports = reportPostService.loadAllExamReports();

        return LoadAllReportedPostForm.builder()
            .evaluatePostReports(evaluatePostReports)
            .examPostReports(examPostReports)
            .build();
    }

    public EvaluatePostReport executeLoadDetailReportedEvaluatePost(Long evaluatePostReportId) {
        return reportPostService.loadDetailEvaluateReportFromReportingEvaluatePostId(evaluatePostReportId);
    }

    public ExamPostReport executeLoadDetailReportedExamPost(Long examPostReportId) {
        return reportPostService.loadDetailEvaluateReportFromReportingExamPostId(examPostReportId);
    }

    public Map<String, Boolean> executeNoProblemEvaluatePost(EvaluatePostNoProblemForm evaluatePostNoProblemForm) {
        reportPostService.deleteByEvaluateIdx(evaluatePostNoProblemForm.evaluateIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeNoProblemExamPost(ExamPostNoProblemForm examPostRestrictForm) {
        reportPostService.deleteByExamIdx(examPostRestrictForm.examIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeRestrictEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm) {
        EvaluatePostReport evaluatePostReport =
          reportPostService.loadDetailEvaluateReportFromReportingEvaluatePostId(evaluatePostRestrictForm.evaluateIdx());

        plusReportingUserPoint(evaluatePostReport.getReportingUserIdx());
        plusRestrictCount(evaluatePostReport.getReportedUserIdx());

        restrictingUserService.executeRestrictUserFromEvaluatePost(evaluatePostRestrictForm, evaluatePostReport.getReportedUserIdx());

        deleteReportedEvaluatePostFromEvaluateIdx(evaluatePostReport.getEvaluateIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeRestrictExamPost(ExamPostRestrictForm examPostRestrictForm) {
        ExamPostReport examPostReport =
          reportPostService.loadDetailEvaluateReportFromReportingExamPostId(examPostRestrictForm.examIdx());

        plusReportingUserPoint(examPostReport.getReportingUserIdx());
        plusRestrictCount(examPostReport.getReportedUserIdx());

        restrictingUserService.executeRestrictUserFromExamPost(examPostRestrictForm, examPostReport.getReportedUserIdx());

        deleteReportedExamPostFromEvaluateIdx(examPostReport.getExamIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeBlackListEvaluatePost(EvaluatePostBlacklistForm evaluatePostBlacklistForm) {
        Long userIdx = evaluatePostCRUDService
            .loadEvaluatePostFromEvaluatePostIdx(evaluatePostBlacklistForm.evaluateIdx())
            .getUser()
            .getId();

        deleteReportedEvaluatePostFromEvaluateIdx(evaluatePostBlacklistForm.evaluateIdx());

        blacklistDomainCRUDService.saveBlackListDomain(
            userIdx,
            BANNED_PERIOD,
            evaluatePostBlacklistForm.bannedReason(),
            evaluatePostBlacklistForm.judgement()
        );
        plusRestrictCount(userIdx);

        return successCapitalFlag();
    }
    
    public Map<String, Boolean> executeBlackListExamPost(ExamPostBlacklistForm examPostBlacklistForm) {
        Long userIdx =
          examPostCRUDService.loadExamPostFromExamPostIdx(examPostBlacklistForm.examIdx()).getUser().getId();

        deleteReportedExamPostFromEvaluateIdx(examPostBlacklistForm.examIdx());
        blacklistDomainCRUDService.saveBlackListDomain(
            userIdx,
            365L,
            examPostBlacklistForm.bannedReason(),
            examPostBlacklistForm.judgement()
        );
        plusRestrictCount(userIdx);

        return successCapitalFlag();
    }

    private void deleteReportedEvaluatePostFromEvaluateIdx(Long evaluateIdx) {
        EvaluatePost evaluatePost = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(evaluateIdx);
        reportPostService.deleteByEvaluateIdx(evaluateIdx);
        evaluatePostCRUDService.delete(evaluatePost);
    }

    private void deleteReportedExamPostFromEvaluateIdx(Long examPostIdx) {
        ExamPost examPost = examPostCRUDService.loadExamPostFromExamPostIdx(examPostIdx);
        reportPostService.deleteByEvaluateIdx(examPostIdx);
        examPostCRUDService.delete(examPost);
    }

    private void plusRestrictCount(Long userIdx) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.increaseRestrictedCountByReportedPost();
        user.editRestricted(true);
    }

    private void plusReportingUserPoint(Long reportingUserIdx) {
        User user = userCRUDService.loadUserFromUserIdx(reportingUserIdx);
        user.increasePointByReporting();
    }
}
