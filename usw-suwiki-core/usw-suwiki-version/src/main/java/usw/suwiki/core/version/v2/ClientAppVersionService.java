package usw.suwiki.core.version.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.VersionException;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClientAppVersionService {

    private final ClientAppVersionRepository clientAppVersionRepository;

    public CheckUpdateMandatoryResponse checkIsUpdateMandatory(String os, int versionCode) {
        ClientOS clientOS = ClientOS.ofString(os);

        ClientAppVersion clientAppVersion = clientAppVersionRepository
            .findFirstByOsAndIsVitalTrueOrderByVersionCodeDesc(clientOS)
            .orElseThrow(() -> new VersionException(ExceptionType.SERVER_ERROR));

        boolean isUpdateMandatory = clientAppVersion.judgeIsUpdateMandatory(clientOS, versionCode);

        return CheckUpdateMandatoryResponse.from(isUpdateMandatory);
    }
}
