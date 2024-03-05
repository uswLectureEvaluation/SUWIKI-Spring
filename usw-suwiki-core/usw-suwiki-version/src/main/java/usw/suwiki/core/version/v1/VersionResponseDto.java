package usw.suwiki.core.version.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VersionResponseDto {
    private final float version;
}
