package usw.suwiki.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamReportRepository extends JpaRepository<ExamPostReport, Long> {

  void deleteByExamIdx(Long examIdx);

  void deleteByReportedUserIdx(Long userIdx);

  void deleteByReportingUserIdx(Long userIdx);
}
