package usw.suwiki.repository.reportTarget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.reportTarget.ReportTarget;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportTargetRepository extends JpaRepository<ReportTarget, Long> {

    @Query(value = "SELECT * FROM report_target", nativeQuery = true)
    List<ReportTarget> loadAllReportedPosts();

}