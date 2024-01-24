package usw.suwiki.domain.version.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.version.entity.ClientAppVersion;
import usw.suwiki.domain.version.entity.ClientOS;

public interface ClientAppVersionRepository extends JpaRepository<ClientAppVersion, Long> {
    Optional<ClientAppVersion> findFirstByOsAndIsVitalTrueOrderByVersionCodeDesc(ClientOS os);

}
