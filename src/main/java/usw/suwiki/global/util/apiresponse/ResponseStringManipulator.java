package usw.suwiki.global.util.apiresponse;

import java.util.Objects;

public class ResponseStringManipulator {
    public static final String KOREAN_NONE = "없음";

    public static String resolveLiteralNull(String value) {     // TODO 데이터 적재 로직 "null" -> null로 변경 후 메서드 삭제
        if (Objects.equals(value, "null")) {
            return KOREAN_NONE;
        }
        return value;
    }

    public static String resolveNull(String value) {
        if (Objects.isNull(value)) {
            return KOREAN_NONE;
        }
        return value;
    }
}
