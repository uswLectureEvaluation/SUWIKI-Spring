package usw.suwiki.domain.lecture;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureToJsonArray {
    Object data;

    Long count;

    public LectureToJsonArray(Object data, Long count) {
        this.data = data;
        this.count = count;
    }

}
