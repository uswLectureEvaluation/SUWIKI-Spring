package usw.suwiki.domain.reportTarget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamReportRepository extends JpaRepository<ExamPostReport, Long> {

    @Query(value = "SELECT * FROM exam_post_report", nativeQuery = true)
    List<ExamPostReport> loadAllReportedPosts();

}