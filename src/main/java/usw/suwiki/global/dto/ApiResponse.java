package usw.suwiki.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    private String code;
    private T data;
    private String message;

    @Builder
    private ApiResponse(String code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public static ApiResponse error(String code, String message) {
        HashMap<String, String> empty = new HashMap<>();
        System.out.println("code = " + code);
        System.out.println("message = " + message);
        return ApiResponse.builder()
                .code(code)
                .data(empty)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, T data, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .data(data)
                .message(message)
                .build();
    }
}
