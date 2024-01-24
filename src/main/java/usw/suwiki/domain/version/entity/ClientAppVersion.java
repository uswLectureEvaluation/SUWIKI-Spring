package usw.suwiki.domain.version.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.global.BaseTimeEntity;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.VersionException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "UNIQUE_OS_AND_VERSION_CODE",
                columnNames = {"os", "version_code"}
        )
})
public class ClientAppVersion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_app_version_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "os")
    private ClientOS os;

    @NotNull
    @Min(value = 0)
    @Column(name = "version_code")
    private Integer versionCode;

    @NotNull
    private Boolean isVital;

    @Size(max = 2000)
    private String description;


    @Builder
    public ClientAppVersion(ClientOS os, Integer versionCode, Boolean isVital, String description) {
        this.os = os;
        this.versionCode = versionCode;
        this.isVital = isVital;
        this.description = description;
    }

    public boolean judgeIsUpdateRequired(ClientOS os, Integer otherVersionCode) {
        if (!this.os.equals(os)) {
            throw new VersionException(ExceptionType.SERVER_ERROR);
        }
        return this.isVital && this.versionCode > otherVersionCode;
    }
}
