package usw.suwiki.domain.lecture.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class LectureFindOption {

    private String orderOption;
    private Integer pageNumber;
    private String majorType;

    public LectureFindOption(String orderOption, Integer pageNumber, String majorType) {
        this.orderOption = orderOption;
        this.pageNumber = pageNumber;
        this.majorType = majorType;
    }

    public boolean passMajorFiltering() {
        return majorType == null || majorType.equals("전체") || majorType.isEmpty();
    }
}
