package usw.suwiki.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse {
    boolean success;

    public static ResultResponse of(boolean result) {
        return ResultResponse.builder()
                .success(result)
                .build();
    }

    public static ResultResponse complete() {
        return ResultResponse.builder()
                .success(true)
                .build();
    }
}
