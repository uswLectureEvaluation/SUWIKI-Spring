package usw.suwiki.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.report.EvaluateReportRepository;
import usw.suwiki.domain.report.ExamReportRepository;
import usw.suwiki.domain.user.service.ClearReportService;

@Service
@Transactional
@RequiredArgsConstructor
class ClearReportServiceImpl implements ClearReportService {
  private final EvaluateReportRepository evaluateReportRepository;
  private final ExamReportRepository examReportRepository;

  @Override
  public void clear(Long userId) {
    examReportRepository.deleteByReportedUserIdx(userId);
    examReportRepository.deleteByReportingUserIdx(userId);
    evaluateReportRepository.deleteAllByReportedUserIdx(userId);
    evaluateReportRepository.deleteAllByReportingUserIdx(userId);
  }
}
