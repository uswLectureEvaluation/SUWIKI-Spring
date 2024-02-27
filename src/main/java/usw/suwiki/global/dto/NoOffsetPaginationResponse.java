package usw.suwiki.global.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
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
