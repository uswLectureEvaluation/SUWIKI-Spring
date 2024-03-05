package usw.suwiki.domain.clientappversion.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import usw.suwiki.domain.version.entity.ClientAppVersion;
import usw.suwiki.domain.version.entity.ClientOS;
import usw.suwiki.domain.version.repository.ClientAppVersionRepository;
import usw.suwiki.domain.version.service.ClientAppVersionService;
import usw.suwiki.global.annotation.SuwikiMockitoTest;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.core.exception.errortype.VersionException;

@SuwikiMockitoTest
public class ClientAppVersionServiceTest {
    @InjectMocks
    ClientAppVersionService clientAppVersionService;

    @Mock
    ClientAppVersionRepository clientAppVersionRepository;


    @Test
    @DisplayName("클라이언트 앱 필수 업데이트 여부 확인")
    public void CLIENT_APP_CHECK_IS_UPDATE_MANDATORY() {
        // given
        ClientAppVersion clientAppVersion = ClientAppVersion.builder()
                .os(ClientOS.ANDROID)
                .versionCode(30)
                .isVital(true)
                .build();

        when(clientAppVersionRepository.findFirstByOsAndIsVitalTrueOrderByVersionCodeDesc(any(ClientOS.class)))
                .thenReturn(Optional.of(clientAppVersion));

        // when & then
        assertThatNoException()
                .isThrownBy(() -> clientAppVersionService.checkIsUpdateMandatory("ANDROID", 20));
        verify(clientAppVersionRepository)
                .findFirstByOsAndIsVitalTrueOrderByVersionCodeDesc(any(ClientOS.class));
    }

    @Test
    @DisplayName("클라이언트 앱 필수 업데이트 여부 확인 실패 - 파라미터는 모두 NOT NULL 이어야 한다.")
    public void CLIENT_APP_CHECK_IS_UPDATE_MANDATORY_FAIL_NULL_PARAMETER() {
        // given

        // when & then
        assertThatThrownBy(() -> clientAppVersionService.checkIsUpdateMandatory(null, 20))
                .isExactlyInstanceOf(VersionException.class)
                .hasMessage(ExceptionType.INVALID_CLIENT_OS.getMessage());
    }

    @Test
    @DisplayName("클라이언트 앱 필수 업데이트 여부 확인 실패 - OS 마다 DB에 최소 하나의 is_vital = true인 레코드가 존재해야 한다.")
    public void CLIENT_APP_CHECK_IS_UPDATE_MANDATORY_FAIL_RECORD_NOT_EXIST() {
        // given
        when(clientAppVersionRepository.findFirstByOsAndIsVitalTrueOrderByVersionCodeDesc(any(ClientOS.class)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> clientAppVersionService.checkIsUpdateMandatory("ANDROID", 30))
                .isExactlyInstanceOf(VersionException.class);
        verify(clientAppVersionRepository)
                .findFirstByOsAndIsVitalTrueOrderByVersionCodeDesc(any(ClientOS.class));
    }

    @Test
    @DisplayName("클라이언트 앱 필수 업데이트 여부 확인 실패 - ClientOS ENUM에 있지 않은 os는 조회될 수 없다.")
    public void CLIENT_APP_CHECK_IS_UPDATE_MANDATORY_FAIL_INVALID_OS() {
        // given

        // when & then
        assertThatThrownBy(() -> clientAppVersionService.checkIsUpdateMandatory("linux_ubuntu", 22))
                .isExactlyInstanceOf(VersionException.class);
    }
}
