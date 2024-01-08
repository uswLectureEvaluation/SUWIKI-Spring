package usw.suwiki.global.dto;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BulkRequest<T> {

    @Valid
    @NotNull
    private List<T> bulk;

    public static <T> BulkRequest<T> of(List<T> bulk) {
        return BulkRequest.<T>builder()
                .bulk(bulk)
                .build();
    }
}
