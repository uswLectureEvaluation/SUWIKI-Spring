package usw.suwiki.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.ReportedPostException;
import usw.suwiki.domain.report.EvaluatePostReport;
import usw.suwiki.domain.report.EvaluateReportRepository;
import usw.suwiki.domain.report.ExamPostReport;
import usw.suwiki.domain.report.ExamReportRepository;
import usw.suwiki.domain.report.model.Report;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
  private final EvaluateReportRepository evaluateReportRepository;
  private final ExamReportRepository examReportRepository;

  public void reportEvaluatePost(Report report) {
    save(report, EvaluatePostReport::from, evaluateReportRepository::save);
  }

  public void reportExamPost(Report report) {
    save(report, ExamPostReport::from, examReportRepository::save);
  }

  private <T> void save(Report report, Function<Report, T> mapper, Consumer<T> repository) {
    T entity = mapper.apply(report);
    repository.accept(entity);
  }

  public List<EvaluatePostReport> loadAllEvaluateReports() {
    return evaluateReportRepository.findAll();
  }

  public List<ExamPostReport> loadAllExamReports() {
    return examReportRepository.findAll();
  }

  public EvaluatePostReport loadEvaluateReportByEvaluateId(Long evaluateId) {
    return evaluateReportRepository.findById(evaluateId)
      .orElseThrow(() -> new ReportedPostException(ExceptionType.REPORTED_POST_NOT_FOUND));
  }

  public ExamPostReport loadExamReportByExamId(Long examId) {
    return examReportRepository.findById(examId)
      .orElseThrow(() -> new ReportedPostException(ExceptionType.REPORTED_POST_NOT_FOUND));
  }

  public void deleteFromUserIdx(Long userId) {
    examReportRepository.deleteByReportedUserIdx(userId);
    examReportRepository.deleteByReportingUserIdx(userId);
    evaluateReportRepository.deleteAllByReportedUserIdx(userId);
    evaluateReportRepository.deleteAllByReportingUserIdx(userId);
  }

  public void deleteByEvaluateIdx(Long evaluateId) {
    evaluateReportRepository.deleteByEvaluateIdx(evaluateId);
  }

  public void deleteByExamIdx(Long examId) {
    examReportRepository.deleteByExamIdx(examId);
  }
}
