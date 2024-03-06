package usw.suwiki.report.exampost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamReportRepository extends JpaRepository<ExamPostReport, Long> {

    void deleteByExamIdx(Long examIdx);

    void deleteByReportedUserIdx(Long userIdx);

    void deleteByReportingUserIdx(Long userIdx);
}
