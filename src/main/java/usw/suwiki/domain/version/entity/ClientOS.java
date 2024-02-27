package usw.suwiki.domain.version.entity;

import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.VersionException;
import usw.suwiki.global.util.enums.KeyValueEnumModel;

@RequiredArgsConstructor
public enum ClientOS implements KeyValueEnumModel<String> {
    ANDROID("ANDROID"), IOS("IOS"), WEB("WEB");

    private final String value;

    public static ClientOS ofString(String param) {
        if (Objects.isNull(param)) {
            throw new VersionException(ExceptionType.INVALID_CLIENT_OS);
        }
        return Arrays.stream(ClientOS.values())
            .filter(v -> v.getValue().equals(param.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new VersionException(ExceptionType.COMMON_CLIENT_ERROR));
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}
