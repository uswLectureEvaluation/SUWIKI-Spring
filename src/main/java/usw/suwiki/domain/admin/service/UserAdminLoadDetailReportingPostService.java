package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import static usw.suwiki.global.exception.ExceptionType.SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminLoadDetailReportingPostService {

    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;

    public EvaluatePostReport executeEvaluatePost(long targetReportingEvaluatePostId) {
        return evaluateReportRepository.findById(targetReportingEvaluatePostId)
                .orElseThrow(() -> new AccountException(SERVER_ERROR));
    }

    public ExamPostReport executeExamPost(long targetReportingExamPostId) {
        return examReportRepository.findById(targetReportingExamPostId)
                .orElseThrow(() -> new AccountException(SERVER_ERROR));
    }
}
