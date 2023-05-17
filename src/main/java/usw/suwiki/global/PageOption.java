package usw.suwiki.global;

import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageOption {

    //    private Optional<String> orderOption;
    private Optional<Integer> pageNumber;

    public PageOption(Optional<Integer> pageNumber) {
        initPageNumber(pageNumber);
    }

    private void initPageNumber(Optional<Integer> pageNumber) {
        if (pageNumber.isEmpty()) {
            this.pageNumber = Optional.of(1);
            return;
        }
        this.pageNumber = pageNumber;
    }

}
