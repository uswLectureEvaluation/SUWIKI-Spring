package usw.suwiki.common.response;

import java.util.Objects;

public final class ResponseFieldManipulationUtils {

    public static final String NONE = "없음";

    public static String resolveLiteralNull(String value) { // TODO 데이터 적재 로직 "null" -> null로 변경 후 메서드 삭제
        return Objects.equals(value, "null") ? NONE : value;
    }

    public static String resolveNull(String value) {
        return Objects.isNull(value) ? NONE : value;
    }
}
