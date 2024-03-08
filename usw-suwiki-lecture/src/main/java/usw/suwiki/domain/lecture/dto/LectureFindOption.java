package usw.suwiki.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LectureFindOption {
    private String orderOption;
    private Integer pageNumber;
    private String majorType;

    public boolean passMajorFiltering() {
        return majorType == null || majorType.equals("전체") || majorType.isEmpty();
    }
}
