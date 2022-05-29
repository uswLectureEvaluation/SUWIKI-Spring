package usw.suwiki.global;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class PageOption {
//    private Optional<String> orderOption;
    private Optional<Integer> pageNumber;

    public PageOption(Optional<Integer> pageNumber) {
        this.pageNumber = pageNumber;
    }

}
