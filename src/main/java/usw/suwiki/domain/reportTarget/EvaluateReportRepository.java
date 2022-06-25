package usw.suwiki.domain.reportTarget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface EvaluateReportRepository extends JpaRepository<EvaluatePostReport, Long> {

    @Query(value = "SELECT * FROM evaluate_post_report", nativeQuery = true)
    List<EvaluatePostReport> loadAllReportedPosts();

    @Transactional
    void deleteByEvaluateIdx(Long evaluateIdx);

}