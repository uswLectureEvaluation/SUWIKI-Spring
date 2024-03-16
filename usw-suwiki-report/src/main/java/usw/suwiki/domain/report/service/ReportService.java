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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
  private final EvaluateReportRepository evaluateReportRepository;
  private final ExamReportRepository examReportRepository;

  public List<EvaluatePostReport> loadAllEvaluateReports() {
    return evaluateReportRepository.findAll();
  }

  public List<ExamPostReport> loadAllExamReports() {
    return examReportRepository.findAll();
  }

  public EvaluatePostReport loadDetailEvaluateReportFromReportingEvaluatePostId(Long reportingEvaluatePostId) {
    return evaluateReportRepository.findById(reportingEvaluatePostId)
      .orElseThrow(() -> new ReportedPostException(ExceptionType.REPORTED_POST_NOT_FOUND));
  }

  public ExamPostReport loadDetailEvaluateReportFromReportingExamPostId(Long reportingExamPostId) {
    return examReportRepository.findById(reportingExamPostId)
      .orElseThrow(() -> new ReportedPostException(ExceptionType.REPORTED_POST_NOT_FOUND));
  }

  public void deleteFromUserIdx(Long userId) {
    examReportRepository.deleteByReportedUserIdx(userId);
    examReportRepository.deleteByReportingUserIdx(userId);
    evaluateReportRepository.deleteAllByReportedUserIdx(userId);
    evaluateReportRepository.deleteAllByReportingUserIdx(userId);
  }

  public void deleteByEvaluateIdx(Long evaluateIdx) {
    evaluateReportRepository.deleteByEvaluateIdx(evaluateIdx);
  }

  public void deleteByExamIdx(Long examIdx) {
    examReportRepository.deleteByExamIdx(examIdx);
  }

  public void saveEvaluatePostReport(EvaluatePostReport evaluatePostReport) {
    evaluateReportRepository.save(evaluatePostReport);
  }

  public void saveExamPostReport(ExamPostReport examPostReport) {
    examReportRepository.save(examPostReport);
  }
}
