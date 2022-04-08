package usw.suwiki.dto.lecture;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class LectureFindOption {
    private Optional<String> orderOption;
    private Optional<Integer> pageNumber;

    public LectureFindOption(Optional<String> orderOption, Optional<Integer> pageNumber) {
        this.orderOption = orderOption;
        this.pageNumber = pageNumber;
    }

}
