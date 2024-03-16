package usw.suwiki.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.service.ClearReportService;
import usw.suwiki.report.EvaluateReportRepository;
import usw.suwiki.report.ExamReportRepository;

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
