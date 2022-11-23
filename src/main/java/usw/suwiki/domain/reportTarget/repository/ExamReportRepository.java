package usw.suwiki.domain.reportTarget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.reportTarget.entity.ExamPostReport;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ExamReportRepository extends JpaRepository<ExamPostReport, Long> {

    @Query(value = "SELECT * FROM exam_post_report", nativeQuery = true)
    List<ExamPostReport> loadAllReportedPosts();

    @Modifying
    void deleteByExamIdx(Long examIdx);

    @Modifying
    @Query(value = "DELETE FROM exam_post_report where reported_user_idx =:userIdx", nativeQuery = true)
    void deleteByUserIdx(@Param("userIdx") Long userIdx);

    @Modifying
    @Query(value = "DELETE FROM exam_post_report where reporting_user_idx =:userIdx", nativeQuery = true)
    void deleteByReportingUserIdx(@Param("userIdx") Long userIdx);

    @Modifying
    @Query(value = "DELETE FROM exam_post_report where reported_user_idx =:userIdx", nativeQuery = true)
    void deleteByReportedUserIdx(@Param("userIdx") Long userIdx);

    Optional<ExamPostReport> findByExamIdx(Long examIdx);

}