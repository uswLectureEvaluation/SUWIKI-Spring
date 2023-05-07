package usw.suwiki.domain.admin.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import usw.suwiki.domain.admin.admin.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.blacklistdomain.BlackListService;
import usw.suwiki.domain.admin.restrictinguser.RestrictingUserService;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.user.user.dto.UserRequestDto;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.service.UserService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtProvider;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static usw.suwiki.global.exception.ExceptionType.*;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.successCapitalFlag;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminService {

    private final BlackListService blackListService;
    private final UserService userService;
    private final ReportPostService reportPostService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final UserAdminService userAdminService;
    private final RestrictingUserService restrictingUserService;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;

    // 관리자 권한 검증
    public void executeValidateAdmin(String authorization) {
        jwtValidator.validateJwt(authorization);
        if (!jwtResolver.getUserRole(authorization).equals("ADMIN")) {
            throw new AccountException(USER_RESTRICTED);
        }
    }

    // 관리자 로그인
    public Map<String, String> executeAdminLogin(UserRequestDto.LoginForm loginForm) {
        if (userService.matchPassword(loginForm.getLoginId(), loginForm.getPassword())) {
            return new HashMap<>() {{
                put("AccessToken", jwtProvider.createAccessToken(userService.loadUserFromLoginId(loginForm.getLoginId())));
                put("UserCount", String.valueOf(userService.findAllUsersSize()));
            }};
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
        return reportPostService.loadDetailEvaluateReportFromReportingEvaluatePostId(evaluatePostReportId);
    }

    public ExamPostReport executeLoadDetailReportedExamPost(Long examPostReportId) {
        return reportPostService.loadDetailEvaluateReportFromReportingExamPostId(examPostReportId);
    }

    public Map<String, Boolean> executeEvaluatePost(UserAdminRequestDto.EvaluatePostNoProblemForm evaluatePostNoProblemForm) {
        reportPostService.deleteByEvaluateIdx(evaluatePostNoProblemForm.getEvaluateIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeExamPost(UserAdminRequestDto.ExamPostNoProblemForm examPostRestrictForm) {
        reportPostService.deleteByExamIdx(examPostRestrictForm.getExamIdx());
        return successCapitalFlag();
    }

    public Map<String, Boolean> executeRestrictEvaluatePost(UserAdminRequestDto.EvaluatePostRestrictForm evaluatePostRestrictForm) {
        restrictingUserService.executeRestrictUserFromEvaluatePost(evaluatePostRestrictForm);
        userAdminService.plusRestrictCount(
                userAdminService.deleteReportedEvaluatePostFromEvaluateIdx(
                        evaluatePostRestrictForm.getEvaluateIdx()));
        userAdminService.plusReportingUserPoint(
                reportPostService.whoIsEvaluateReporting(
                        evaluatePostRestrictForm.getEvaluateIdx()));

        return successCapitalFlag();
    }

    public Map<String, Boolean> executeRestrictExamPost(UserAdminRequestDto.ExamPostRestrictForm examPostRestrictForm) {
        restrictingUserService.executeRestrictUserFromExamPost(examPostRestrictForm);
        userAdminService.plusRestrictCount(
                userAdminService.deleteReportedExamPostFromEvaluateIdx(
                        examPostRestrictForm.getExamIdx()));
        userAdminService.plusReportingUserPoint(
                reportPostService.whoIsExamReporting(
                        examPostRestrictForm.getExamIdx()));

        return successCapitalFlag();
    }

    public Map<String, Boolean> executeBlackListEvaluatePost(
            EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {

        Long userIdx = evaluatePostsService
                .loadEvaluatePostsFromEvaluatePostsIdx(
                        evaluatePostBlacklistForm.getEvaluateIdx())
                .getUser()
                .getId();

        userAdminService.deleteReportedEvaluatePostFromEvaluateIdx(
                evaluatePostBlacklistForm.getEvaluateIdx()
        );

        blackListService.executeBlacklist(
                userIdx,
                365L,
                evaluatePostBlacklistForm.getBannedReason(),
                evaluatePostBlacklistForm.getJudgement()
        );
        userAdminService.plusRestrictCount(userIdx);

        return successCapitalFlag();
    }

    public Map<String, Boolean> executeBlackListExamPost(
            ExamPostBlacklistForm examPostBlacklistForm
    ) {
        Long userIdx = examPostsService.loadExamPostsFromExamPostsIdx(
                examPostBlacklistForm.getExamIdx()).getUser().getId();

        userAdminService.deleteReportedExamPostFromEvaluateIdx(
                examPostBlacklistForm.getExamIdx()
        );

        blackListService.executeBlacklist(
                userIdx,
                365L,
                examPostBlacklistForm.getBannedReason(),
                examPostBlacklistForm.getJudgement()
        );
        userAdminService.plusRestrictCount(userIdx);

        return successCapitalFlag();
    }

    public Long deleteReportedEvaluatePostFromEvaluateIdx(Long evaluateIdx) {
        if (evaluatePostsService.loadEvaluatePostsFromEvaluatePostsIdx(evaluateIdx) != null) {
            EvaluatePosts evaluatePost = evaluatePostsService.loadEvaluatePostsFromEvaluatePostsIdx(evaluateIdx);
            reportPostService.deleteByEvaluateIdx(evaluateIdx);
            evaluatePostsService.executeDeleteEvaluatePost(
                    evaluatePost.getId(),
                    evaluatePost.getUser().getId()
            );
            return evaluatePost.getUser().getId();
        }

        throw new AccountException(SERVER_ERROR);
    }

    public Long deleteReportedExamPostFromEvaluateIdx(Long examPostIdx) {
        if (examPostsService.loadExamPostsFromExamPostsIdx(examPostIdx) != null) {
            ExamPosts examPost = examPostsService.loadExamPostsFromExamPostsIdx(examPostIdx);
            reportPostService.deleteByEvaluateIdx(examPostIdx);
            evaluatePostsService.executeDeleteEvaluatePost(
                    examPost.getId(),
                    examPost.getUser().getId()
            );
            return examPost.getUser().getId();
        }
        throw new AccountException(SERVER_ERROR);
    }

    public void plusRestrictCount(Long userIdx) {
        User user = userService.loadUserFromUserIdx(userIdx);
        user.increaseRestrictedCountByReportedPost();
    }

    public void plusReportingUserPoint(Long reportingUserIdx) {
        User user = userService.loadUserFromUserIdx(reportingUserIdx);
        user.increasePointByReporting();
    }

    public Long whoIsEvaluateReporting(Long evaluateIdx) {
        return reportPostService.loadDetailEvaluateReportFromReportingEvaluatePostId(evaluateIdx).getReportingUserIdx();
    }
}
