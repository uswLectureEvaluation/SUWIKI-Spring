package usw.suwiki.global.util;

import java.util.HashMap;
import java.util.Map;

public class ApiResponseFactory {

    public static Map<String, Boolean> successFlag() {
        return new HashMap<>() {{
            put("success", true);
        }};
    }
}
