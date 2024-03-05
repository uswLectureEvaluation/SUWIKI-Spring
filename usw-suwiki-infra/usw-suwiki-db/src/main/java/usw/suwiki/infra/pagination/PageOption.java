package usw.suwiki.infra.pagination;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class PageOption {

    private Optional<Integer> pageNumber;

    public PageOption(Optional<Integer> pageNumber) {
        initPageNumber(pageNumber);
    }

    public Optional<Integer> getPageNumber() {
        return Optional.of(pageNumber.get() - 1);
    }

    public Integer getOffset() {
        return getPageNumber().get() * 10;
    }

    private void initPageNumber(Optional<Integer> pageNumber) {
        if (pageNumber.isEmpty()) {
            this.pageNumber = Optional.of(1);
            return;
        }
        this.pageNumber = pageNumber;
    }
}
