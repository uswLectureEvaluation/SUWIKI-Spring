package usw.suwiki.domain.version.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.global.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientAppVersion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_version_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ClientOS os;

    @NotNull
    @Min(value = 0)
    private Integer versionCode;

    @NotNull
    private Boolean isUpdateRequired;

    @Size(max = 2000)
    private String description;


    @Builder
    public ClientAppVersion(ClientOS os, Integer versionCode, Boolean isUpdateRequired, String description) {
        this.os = os;
        this.versionCode = versionCode;
        this.isUpdateRequired = isUpdateRequired;
        this.description = description;
    }
}
