package usw.suwiki.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NoOffsetPaginationResponse<T> {

    Boolean isLast;
    List<T> content;

    public static <T> NoOffsetPaginationResponse<T> of(Slice<T> slice) {
        return NoOffsetPaginationResponse.<T>builder()
            .content(slice.getContent())
            .isLast(slice.isLast())
            .build();
    }

    public static <T> NoOffsetPaginationResponse<T> of(List<T> content, boolean isLast) {
        return NoOffsetPaginationResponse.<T>builder()
            .content(content)
            .isLast(isLast)
            .build();
    }

    public static <T> NoOffsetPaginationResponse<T> of(Page<T> page) {
        return NoOffsetPaginationResponse.<T>builder()
            .content(page.getContent())
            .isLast(page.isLast())
            .build();
    }
}
