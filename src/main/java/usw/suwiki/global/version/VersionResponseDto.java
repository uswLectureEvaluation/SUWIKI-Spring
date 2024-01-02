package usw.suwiki.global.version;

import lombok.Getter;

@Getter
public class VersionResponseDto {

    private final float version;

    public VersionResponseDto(float version) {
        this.version = version;
    }
}
