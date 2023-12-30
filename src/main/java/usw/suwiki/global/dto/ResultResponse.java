package usw.suwiki.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse {
    boolean result;

    public static ResultResponse of(boolean result) {
        return ResultResponse.builder()
                .result(result)
                .build();
    }
}
