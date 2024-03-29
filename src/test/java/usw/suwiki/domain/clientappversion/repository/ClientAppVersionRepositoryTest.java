package usw.suwiki.domain.clientappversion.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import usw.suwiki.domain.version.entity.ClientAppVersion;
import usw.suwiki.domain.version.entity.ClientOS;
import usw.suwiki.domain.version.repository.ClientAppVersionRepository;
import usw.suwiki.global.annotation.SuwikiJpaTest;
import usw.suwiki.global.exception.errortype.VersionException;

@SuwikiJpaTest
public class ClientAppVersionRepositoryTest {
    @Autowired
    ClientAppVersionRepository clientAppVersionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private ClientAppVersion dummyClientAppVersionFirst;
    private ClientAppVersion dummyClientAppVersionSecond;
    private ClientAppVersion dummyClientAppVersionThird;


    @BeforeEach
    public void setUp() {
        ClientAppVersion androidVersion1 = ClientAppVersion.builder()
                .os(ClientOS.ANDROID)
                .versionCode(1)
                .isVital(true)
                .build();
        ClientAppVersion androidVersion2 = ClientAppVersion.builder()
                .os(ClientOS.ANDROID)
                .versionCode(2)
                .isVital(true)
                .build();
        ClientAppVersion androidVersion3 = ClientAppVersion.builder()
                .os(ClientOS.ANDROID)
                .versionCode(3)
                .isVital(false)
                .build();

        this.dummyClientAppVersionFirst = clientAppVersionRepository.save(androidVersion1);
        this.dummyClientAppVersionSecond = clientAppVersionRepository.save(androidVersion2);
        this.dummyClientAppVersionThird = clientAppVersionRepository.save(androidVersion3);

        entityManager.clear();
    }


    @Test
    @DisplayName("클라이언트 앱 버전 생성")
    public void CLIENT_APP_VERSION_CREATE() {
        // given
        ClientAppVersion iosAppVersion = ClientAppVersion.builder()
                .os(ClientOS.IOS)
                .versionCode(1)
                .isVital(true)
                .build();

        // when
        ClientAppVersion saved = clientAppVersionRepository.save(iosAppVersion);
        entityManager.clear();
        Optional<ClientAppVersion> found = clientAppVersionRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getOs()).isEqualTo(ClientOS.IOS);
        assertThat(found.get().getVersionCode()).isEqualTo(1);
        assertThat(found.get().getIsVital()).isTrue();
    }

    @Test
    @DisplayName("클라이언트 앱 버전 생성 실패 - NOT NULL 제약 조건을 준수해야 한다.")
    public void CLIENT_APP_VERSION_CREATE_FAIL_NOT_NULL_CONSTRAINT() {
        // given
        ClientAppVersion nullOsVersion = ClientAppVersion.builder()
                .versionCode(1)
                .isVital(true)
                .build();
        ClientAppVersion nullCodeVersion = ClientAppVersion.builder()
                .os(ClientOS.IOS)
                .isVital(true)
                .build();
        ClientAppVersion nullIsVitalVersion = ClientAppVersion.builder()
                .os(ClientOS.IOS)
                .versionCode(1)
                .build();

        // when & then
        assertThatThrownBy(() -> clientAppVersionRepository.save(nullOsVersion))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> clientAppVersionRepository.save(nullCodeVersion))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> clientAppVersionRepository.save(nullIsVitalVersion))
                .isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("클라이언트 앱 버전 생성 실패 - UNIQUE 제약 조건 (os, versionCode)을 준수해야 한다.")
    public void CLIENT_APP_VERSION_CREATE_FAIL_UNIQUE_CONSTRAINT() {
        // given
        ClientAppVersion first = ClientAppVersion.builder()
                .os(ClientOS.IOS)
                .versionCode(1)
                .isVital(false)
                .build();
        ClientAppVersion second = ClientAppVersion.builder()
                .os(ClientOS.IOS)
                .versionCode(1)
                .isVital(false)
                .build();

        // when & then
        assertThatNoException().isThrownBy(() -> clientAppVersionRepository.save(first));
        assertThatThrownBy(() -> clientAppVersionRepository.save(second))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("클라이언트 앱 버전 조회 - 특정 OS에 대한 가장 최신의 필수 버전 조회")
    public void CLIENT_APP_VERSION_FIND_MOST_RECENT_VITAL_VERSION() {
        // given
        Optional<ClientAppVersion> optionalClientAppVersion = clientAppVersionRepository
                .findFirstByOsAndIsVitalTrueOrderByVersionCodeDesc(ClientOS.ANDROID);

        // when & then
        assertThat(optionalClientAppVersion).isPresent();
        assertThat(optionalClientAppVersion.get().getVersionCode()).isEqualTo(2);
    }

    @Test
    @DisplayName("클라이언트 앱 버전 조회 - 업데이트 필수 여부 확인")
    public void CLIENT_APP_VERSION_CHECK_IS_UPDATE_REQUIRED() {
        // given
        Optional<ClientAppVersion> optionalClientAppVersion = clientAppVersionRepository
                .findFirstByOsAndIsVitalTrueOrderByVersionCodeDesc(ClientOS.ANDROID);

        // when & then
        assertThat(optionalClientAppVersion).isPresent();
        assertThat(optionalClientAppVersion.get().judgeIsUpdateMandatory(ClientOS.ANDROID, 1))
                .isTrue();
        assertThatThrownBy(() -> optionalClientAppVersion.get().judgeIsUpdateMandatory(ClientOS.IOS, 1))
                .isExactlyInstanceOf(VersionException.class);
    }


}
