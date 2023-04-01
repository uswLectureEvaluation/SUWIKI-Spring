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
        this.pageNumber = pageNumber;
    }

}
