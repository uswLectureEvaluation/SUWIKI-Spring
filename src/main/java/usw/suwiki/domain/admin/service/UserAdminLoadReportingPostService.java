package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.service.UserAdminCommonService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminLoadReportingPostService {

    private final UserAdminCommonService userAdminCommonService;

    public LoadAllReportedPostForm execute() {
        List<EvaluatePostReport> evaluatePostReports = userAdminCommonService.loadReportedEvaluateList();
        List<ExamPostReport> examPostReports = userAdminCommonService.loadReportedExamList();

        return LoadAllReportedPostForm
                .builder()
                .evaluatePostReports(evaluatePostReports)
                .examPostReports(examPostReports)
                .build();
    }

}
