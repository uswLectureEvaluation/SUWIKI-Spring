package usw.suwiki.global.util.apiresponse;

import java.util.HashMap;
import java.util.Map;

public class ApiResponseFactory {

    public static Map<String, String> adminLoginResponseForm(
        final String accessToken,
        final String userCount
    ) {
        return new HashMap<>() {{
            put("AccessToken", accessToken);
            put("UserCount", userCount);
        }};
    }

    public static Map<String, Boolean> successFlag() {
        return new HashMap<>() {{
            put("success", true);
        }};
    }

    public static Map<String, Boolean> successCapitalFlag() {
        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    public static Map<String, Boolean> overlapTrueFlag() {
        return new HashMap<>() {{
            put("overlap", true);
        }};
    }

    public static Map<String, Boolean> overlapFalseFlag() {
        return new HashMap<>() {{
            put("overlap", false);
        }};
    }
}
