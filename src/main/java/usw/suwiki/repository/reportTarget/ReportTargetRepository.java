package usw.suwiki.repository.reportTarget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.reportTarget.ReportTarget;

@Repository
public interface ReportTargetRepository extends JpaRepository<ReportTarget, Long> {
}
