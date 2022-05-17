package usw.suwiki.repository.reportTarget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.reportTarget.EvaluatePostReport;

import java.util.List;

@Repository
public interface EvaluateReportRepository extends JpaRepository<EvaluatePostReport, Long> {

    @Query(value = "SELECT * FROM evaluate_post_report", nativeQuery = true)
    List<EvaluatePostReport> loadAllReportedPosts();

}