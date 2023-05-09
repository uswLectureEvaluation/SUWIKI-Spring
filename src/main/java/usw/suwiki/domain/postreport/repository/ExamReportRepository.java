package usw.suwiki.domain.postreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.postreport.ExamPostReport;

@Repository
@Transactional
public interface ExamReportRepository extends JpaRepository<ExamPostReport, Long> {

    void deleteByExamIdx(Long examIdx);

    @Query(value = "DELETE FROM exam_post_report where reported_user_idx =:userIdx", nativeQuery = true)
    void deleteByUserIdx(@Param("userIdx") Long userIdx);

    @Query(value = "DELETE FROM exam_post_report where reporting_user_idx =:userIdx", nativeQuery = true)
    void deleteByReportingUserIdx(@Param("userIdx") Long userIdx);

    @Query(value = "DELETE FROM exam_post_report where reported_user_idx =:userIdx", nativeQuery = true)
    void deleteByReportedUserIdx(@Param("userIdx") Long userIdx);

}