package usw.suwiki.report.evaluatepost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluateReportRepository extends JpaRepository<EvaluatePostReport, Long> {

    @Query(value = "SELECT * FROM evaluate_post_report", nativeQuery = true)
    List<EvaluatePostReport> loadAllReportedPosts();

    void deleteByEvaluateIdx(Long evaluateIdx);

    @Modifying
    @Query(value = "DELETE FROM evaluate_post_report where reporting_user_idx =:userIdx", nativeQuery = true)
    void deleteByReportingUserIdx(@Param("userIdx") Long userIdx);

    @Modifying
    @Query(value = "DELETE FROM evaluate_post_report where reported_user_idx =:userIdx", nativeQuery = true)
    void deleteByReportedUserIdx(@Param("userIdx") Long userIdx);

    Optional<EvaluatePostReport> findByEvaluateIdx(Long evaluateIdx);
}
