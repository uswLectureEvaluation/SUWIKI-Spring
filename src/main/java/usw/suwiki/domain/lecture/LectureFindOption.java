package usw.suwiki.domain.lecture;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class LectureFindOption {
    private Optional<String> orderOption;
    private Optional<Integer> pageNumber;
    private Optional<String> majorType;

    @Builder
    public LectureFindOption(Optional<String> orderOption, Optional<Integer> pageNumber, Optional<String> majorType) {
        this.orderOption = orderOption;
        this.pageNumber = pageNumber;
        if (majorType.isEmpty()) {
            this.majorType = Optional.of("");
        } else {
            this.majorType = majorType;
        }
    }

}
