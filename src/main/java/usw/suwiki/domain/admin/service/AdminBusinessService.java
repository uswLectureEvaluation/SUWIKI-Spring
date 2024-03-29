package usw.suwiki.domain.admin.service;

import static usw.suwiki.global.exception.ExceptionType.PASSWORD_ERROR;
import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.adminLoginResponseForm;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.successCapitalFlag;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostNoProblemForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.ExamPostNoProblemForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exampost.domain.ExamPost;
import usw.suwiki.domain.exampost.service.ExamPostCRUDService;
import usw.suwiki.domain.postreport.EvaluatePostReport;
import usw.suwiki.domain.postreport.ExamPostReport;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.domain.user.userIsolation.service.UserIsolationCRUDService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminBusinessService {

    private static final long BANNED_PERIOD = 365L;

    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final UserCRUDService userCRUDService;
    private final UserIsolationCRUDService userIsolationCRUDService;
    private final ReportPostService reportPostService;
    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final ExamPostCRUDService examPostCRUDService;
    private final RestrictingUserService restrictingUserService;
    private final JwtAgent jwtAgent;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Map<String, String> executeAdminLogin(LoginForm loginForm) {
        User user = userCRUDService.loadUserFromLoginId(loginForm.loginId());
        if (user.validatePassword(bCryptPasswordEncoder, loginForm.password())) {
            if (user.isAdmin()) {
                final long userCount = userCRUDService.countAllUsers();
                final long userIsolationCount = userIsolationCRUDService.countAllIsolatedUsers();
                final long totalUserCount = userCount + userIsolationCount;

                return adminLoginResponseForm(
                    jwtAgent.createAccessToken(user),
                    String.valueOf(totalUserCount)
                );
            }
            throw new AccountException(USER_RESTRICTED);
        }
        throw new AccountException(PASSWORD_ERROR);
    }

    public LoadAllReportedPostForm executeLoadAllReportedPosts() {
        List<EvaluatePostReport> evaluatePostReports = reportPostService.loadAllEvaluateReports();
        List<ExamPostReport> examPostReports = reportPostService.loadAllExamReports();

        return LoadAllReportedPostForm
            .builder()
            .evaluatePostReports(evaluatePostReports)
            .examPostReports(examPostReports)
            .build();
    }

    public EvaluatePostReport executeLoadDetailReportedEvaluatePost(Long evaluatePostReportId) {
        return reportPostService.loadDetailEvaluateReportFromReportingEvaluatePostId(
            evaluatePostReportId);
    }

    public ExamPostReport executeLoadDetailReportedExamPost(Long examPostReportId) {
        return reportPostService.loadDetailEvaluateReportFromReportingExamPostId(examPostReportId);
    }

    public Map<String, Boolean> executeNoProblemEvaluatePost(
        EvaluatePostNoProblemForm evaluatePostNoProblemForm) {
        reportPostService.deleteByEvaluateIdx(evaluatePostNoProblemForm.evaluateIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeNoProblemExamPost(
        ExamPostNoProblemForm examPostRestrictForm) {
        reportPostService.deleteByExamIdx(examPostRestrictForm.examIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeRestrictEvaluatePost(
        EvaluatePostRestrictForm evaluatePostRestrictForm) {
        EvaluatePostReport evaluatePostReport = reportPostService.loadDetailEvaluateReportFromReportingEvaluatePostId(
            evaluatePostRestrictForm.evaluateIdx());
        plusReportingUserPoint(evaluatePostReport.getReportingUserIdx());
        plusRestrictCount(evaluatePostReport.getReportedUserIdx());
        restrictingUserService.executeRestrictUserFromEvaluatePost(
            evaluatePostRestrictForm, evaluatePostReport.getReportedUserIdx()
        );
        deleteReportedEvaluatePostFromEvaluateIdx(evaluatePostReport.getEvaluateIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeRestrictExamPost(ExamPostRestrictForm examPostRestrictForm) {
        ExamPostReport examPostReport = reportPostService.loadDetailEvaluateReportFromReportingExamPostId(
            examPostRestrictForm.examIdx());
        plusReportingUserPoint(examPostReport.getReportingUserIdx());
        plusRestrictCount(examPostReport.getReportedUserIdx());
        restrictingUserService.executeRestrictUserFromExamPost(
            examPostRestrictForm, examPostReport.getReportedUserIdx()
        );
        deleteReportedExamPostFromEvaluateIdx(examPostReport.getExamIdx());

        return successCapitalFlag();
    }

    public Map<String, Boolean> executeBlackListEvaluatePost(
        EvaluatePostBlacklistForm evaluatePostBlacklistForm) {
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
    
    public Map<String, Boolean> executeBlackListExamPost(
        ExamPostBlacklistForm examPostBlacklistForm) {
        Long userIdx = examPostCRUDService.loadExamPostFromExamPostIdx(
            examPostBlacklistForm.examIdx()).getUser().getId();

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
        EvaluatePost evaluatePost = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(
            evaluateIdx);
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
