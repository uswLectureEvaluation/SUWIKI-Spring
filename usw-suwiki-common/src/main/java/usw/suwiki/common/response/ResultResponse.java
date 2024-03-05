package usw.suwiki.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse {

    private final boolean success;

    public static ResultResponse of(boolean result) {
        return new ResultResponse(result);
    }

    public static ResultResponse complete() {
        return new ResultResponse(true);
    }
}
