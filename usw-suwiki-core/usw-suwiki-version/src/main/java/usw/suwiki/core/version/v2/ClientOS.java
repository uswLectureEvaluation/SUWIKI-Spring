package usw.suwiki.core.version.v2;

import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.VersionException;

import java.util.Objects;

public enum ClientOS {
  ANDROID,
  IOS,
  WEB;

  public static ClientOS ofString(String param) {
    checkNotNull(param);

    try {
      return ClientOS.valueOf(param.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new VersionException(ExceptionType.COMMON_CLIENT_ERROR);
    }
  }

  private static void checkNotNull(String param) {
    if (Objects.isNull(param)) {
      throw new VersionException(ExceptionType.INVALID_CLIENT_OS);
    }
  }
}
