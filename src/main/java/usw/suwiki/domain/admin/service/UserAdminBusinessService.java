package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto;
import usw.suwiki.domain.admin.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.evaluation.domain.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostCRUDService;
import usw.suwiki.domain.postreport.EvaluatePostReport;
import usw.suwiki.domain.postreport.ExamPostReport;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static usw.suwiki.global.exception.ExceptionType.PASSWORD_ERROR;
import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.successCapitalFlag;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminBusinessService {

    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final UserCRUDService userCRUDService;
    private final ReportPostService reportPostService;
    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final ExamPostCRUDService examPostCRUDService;
    private final RestrictingUserService restrictingUserService;
    private final JwtAgent jwtAgent;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 관리자 로그인
     */
    public Map<String, String> executeAdminLogin(LoginForm loginForm) {
        User user = userCRUDService.loadUserFromLoginId(loginForm.getLoginId());
        if (bCryptPasswordEncoder.matches(loginForm.getPassword(), user.getPassword())) {
            if (user.getRole().getKey().equals("ADMIN")) {
                return new HashMap<>() {{
                    put("AccessToken", jwtAgent.createAccessToken(user));
                    put("UserCount", String.valueOf(userCRUDService.findAllUsersSize()));
                }};
            }
            throw new AccountException(USER_RESTRICTED);
        }
        throw new AccountException(PASSWORD_ERROR);
    }

    /**
     * 신고된 모든 게시글 조회
     */
    public LoadAllReportedPostForm executeLoadAllReportedPosts(String accessToken) {
        validateAdmin(accessToken);
        List<EvaluatePostReport> evaluatePostReports = reportPostService.loadAllEvaluateReports();
        List<ExamPostReport> examPostReports = reportPostService.loadAllExamReports();

        return LoadAllReportedPostForm
                .builder()
                .evaluatePostReports(evaluatePostReports)
                .examPostReports(examPostReports)
                .build();
    }

    /**
     * 신고된 강의평가 게시물 자세히 보기
     */
    public EvaluatePostReport executeLoadDetailReportedEvaluatePost(String accessToken, Long evaluatePostReportId) {
        validateAdmin(accessToken);
        return reportPostService.loadDetailEvaluateReportFromReportingEvaluatePostId(evaluatePostReportId);
    }

    /**
     * 신고된 시험정보 게시물 자세히 보기
     */
    public ExamPostReport executeLoadDetailReportedExamPost(String accessToken, Long examPostReportId) {
        validateAdmin(accessToken);
        return reportPostService.loadDetailEvaluateReportFromReportingExamPostId(examPostReportId);
    }

    /**
     * 신고된 강의평가 게시물 삭제
     */
    public Map<String, Boolean> executeNoProblemEvaluatePost(String accessToken, UserAdminRequestDto.EvaluatePostNoProblemForm evaluatePostNoProblemForm) {
        validateAdmin(accessToken);
        reportPostService.deleteByEvaluateIdx(evaluatePostNoProblemForm.getEvaluateIdx());
        return successCapitalFlag();
    }

    /**
     * 신고된 시험정보 게시물 삭제
     */
    public Map<String, Boolean> executeNoProblemExamPost(String accessToken, UserAdminRequestDto.ExamPostNoProblemForm examPostRestrictForm) {
        validateAdmin(accessToken);
        reportPostService.deleteByExamIdx(examPostRestrictForm.getExamIdx());
        return successCapitalFlag();
    }

    /**
     * 신고된 강의평가 게시물 작성자 이용 정지 처리
     */
    public Map<String, Boolean> executeRestrictEvaluatePost(String accessToken, UserAdminRequestDto.EvaluatePostRestrictForm evaluatePostRestrictForm) {
        validateAdmin(accessToken);
        restrictingUserService.executeRestrictUserFromEvaluatePost(evaluatePostRestrictForm);
        plusRestrictCount(deleteReportedEvaluatePostFromEvaluateIdx(evaluatePostRestrictForm.getEvaluateIdx()));
        plusReportingUserPoint(reportPostService.whoIsEvaluateReporting(evaluatePostRestrictForm.getEvaluateIdx()));

        return successCapitalFlag();
    }

    /**
     * 신고된 시험정보 게시물 작성자 이용 정지 처리
     */
    public Map<String, Boolean> executeRestrictExamPost(String accessToken, UserAdminRequestDto.ExamPostRestrictForm examPostRestrictForm) {
        validateAdmin(accessToken);
        restrictingUserService.executeRestrictUserFromExamPost(examPostRestrictForm);
        plusRestrictCount(deleteReportedExamPostFromEvaluateIdx(examPostRestrictForm.getExamIdx()));
        plusReportingUserPoint(reportPostService.whoIsExamReporting(examPostRestrictForm.getExamIdx()));

        return successCapitalFlag();
    }

    /**
     * 신고된 강의평가 게시물 작성자 블랙리스트 처리
     */
    public Map<String, Boolean> executeBlackListEvaluatePost(
            String accessToken,
            UserAdminRequestDto.EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {
        validateAdmin(accessToken);
        Long userIdx = evaluatePostCRUDService
                .loadEvaluatePostFromEvaluatePostIdx(evaluatePostBlacklistForm.getEvaluateIdx())
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

    /**
     * 신고된 시험정보 게시물 작성자 블랙리스트 처리
     */
    public Map<String, Boolean> executeBlackListExamPost(
            String accessToken,
            UserAdminRequestDto.ExamPostBlacklistForm examPostBlacklistForm
    ) {
        validateAdmin(accessToken);
        Long userIdx = examPostCRUDService.loadExamPostFromExamPostIdx(examPostBlacklistForm.getExamIdx()).getUser().getId();

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

    private Long deleteReportedEvaluatePostFromEvaluateIdx(Long evaluateIdx) {
        EvaluatePosts evaluatePost = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(evaluateIdx);
        reportPostService.deleteByEvaluateIdx(evaluateIdx);
        evaluatePostCRUDService.delete(evaluatePost);
        return evaluatePost.getUser().getId();
    }

    private Long deleteReportedExamPostFromEvaluateIdx(Long examPostIdx) {
        ExamPosts examPost = examPostCRUDService.loadExamPostFromExamPostIdx(examPostIdx);
        reportPostService.deleteByEvaluateIdx(examPostIdx);
        examPostCRUDService.delete(examPost);
        return examPost.getUser().getId();
    }

    private void plusRestrictCount(Long userIdx) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.increaseRestrictedCountByReportedPost();
    }

    private void plusReportingUserPoint(Long reportingUserIdx) {
        User user = userCRUDService.loadUserFromUserIdx(reportingUserIdx);
        user.increasePointByReporting();
    }

    /**
     * 관리자 권한 검증
     */
    private void validateAdmin(String authorization) {
        jwtAgent.validateJwt(authorization);
        if (!jwtAgent.getUserRole(authorization).equals("ADMIN")) {
            throw new AccountException(USER_RESTRICTED);
        }
    }
}