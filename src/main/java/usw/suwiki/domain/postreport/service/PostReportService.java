package usw.suwiki.domain.postreport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class PostReportService {

    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;

    public void deleteByUserIdx(Long userId) {
        examReportRepository.deleteByReportedUserIdx(userId);
        examReportRepository.deleteByReportingUserIdx(userId);
        evaluateReportRepository.deleteByReportedUserIdx(userId);
        evaluateReportRepository.deleteByReportingUserIdx(userId);
    }

}
