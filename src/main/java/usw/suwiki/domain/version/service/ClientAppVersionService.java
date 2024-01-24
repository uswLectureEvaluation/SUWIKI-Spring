package usw.suwiki.domain.version.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.version.dto.response.CheckUpdateMandatoryResponse;
import usw.suwiki.domain.version.entity.ClientAppVersion;
import usw.suwiki.domain.version.entity.ClientOS;
import usw.suwiki.domain.version.repository.ClientAppVersionRepository;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.VersionException;


@Slf4j
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
