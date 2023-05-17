package usw.suwiki.domain.postreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.postreport.ExamPostReport;

@Repository
public interface ExamReportRepository extends JpaRepository<ExamPostReport, Long> {

    void deleteByExamIdx(Long examIdx);

    void deleteByReportedUserIdx(Long userIdx);

    void deleteByReportingUserIdx(Long userIdx);
}