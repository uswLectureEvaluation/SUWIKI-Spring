package usw.suwiki.domain.admin.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import usw.suwiki.domain.admin.admin.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.admin.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
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

    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final UserCRUDService userCRUDService;
    private final ReportPostService reportPostService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final RestrictingUserService restrictingUserService;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 관리자 권한 검증
    public void executeValidateAdmin(String authorization) {
        jwtValidator.validateJwt(authorization);
        if (!jwtResolver.getUserRole(authorization).equals("ADMIN")) {
            throw new AccountException(USER_RESTRICTED);
        }
    }

    // 관리자 로그인
    public Map<String, String> executeAdminLogin(LoginForm loginForm) {
        if (bCryptPasswordEncoder.matches(loginForm.getLoginId(), loginForm.getPassword())) {
            return new HashMap<>() {{
                put("AccessToken", jwtProvider.createAccessToken(userCRUDService.loadUserFromLoginId(loginForm.getLoginId())));
                put("UserCount", String.valueOf(userCRUDService.findAllUsersSize()));
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
        plusRestrictCount(deleteReportedEvaluatePostFromEvaluateIdx(evaluatePostRestrictForm.getEvaluateIdx()));
        plusReportingUserPoint(reportPostService.whoIsEvaluateReporting(evaluatePostRestrictForm.getEvaluateIdx()));

        return successCapitalFlag();
    }

    public Map<String, Boolean> executeRestrictExamPost(UserAdminRequestDto.ExamPostRestrictForm examPostRestrictForm) {
        restrictingUserService.executeRestrictUserFromExamPost(examPostRestrictForm);
        plusRestrictCount(deleteReportedExamPostFromEvaluateIdx(examPostRestrictForm.getExamIdx()));
        plusReportingUserPoint(reportPostService.whoIsExamReporting(examPostRestrictForm.getExamIdx()));

        return successCapitalFlag();
    }

    public Map<String, Boolean> executeBlackListEvaluatePost(
            EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {

        Long userIdx = evaluatePostsService
                .loadEvaluatePostsFromEvaluatePostsIdx(evaluatePostBlacklistForm.getEvaluateIdx())
                .getUser()
                .getId();

        deleteReportedEvaluatePostFromEvaluateIdx(evaluatePostBlacklistForm.getEvaluateIdx());
        blacklistDomainCRUDService.saveBlackListDomain(
                userIdx,
                365L,
                evaluatePostBlacklistForm.getBannedReason(),
                evaluatePostBlacklistForm.getJudgement()
        );
        plusRestrictCount(userIdx);

        return successCapitalFlag();
    }

    public Map<String, Boolean> executeBlackListExamPost(
            ExamPostBlacklistForm examPostBlacklistForm
    ) {
        Long userIdx = examPostsService.loadExamPostsFromExamPostsIdx(examPostBlacklistForm.getExamIdx()).getUser().getId();

        deleteReportedExamPostFromEvaluateIdx(examPostBlacklistForm.getExamIdx());
        blacklistDomainCRUDService.saveBlackListDomain(
                userIdx,
                365L,
                examPostBlacklistForm.getBannedReason(),
                examPostBlacklistForm.getJudgement()
        );
        plusRestrictCount(userIdx);

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
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.increaseRestrictedCountByReportedPost();
    }

    public void plusReportingUserPoint(Long reportingUserIdx) {
        User user = userCRUDService.loadUserFromUserIdx(reportingUserIdx);
        user.increasePointByReporting();
    }
}
