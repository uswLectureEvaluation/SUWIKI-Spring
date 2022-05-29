package usw.suwiki.global;

import lombok.Getter;

@Getter
public class VersionResponseDto {
    private float version;

    public VersionResponseDto(float version) {
        this.version = version;
    }
}
