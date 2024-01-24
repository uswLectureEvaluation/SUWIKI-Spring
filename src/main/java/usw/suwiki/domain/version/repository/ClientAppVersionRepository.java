package usw.suwiki.domain.version.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.version.entity.ClientAppVersion;

public interface ClientAppVersionRepository extends JpaRepository<ClientAppVersion, Long> {

}
