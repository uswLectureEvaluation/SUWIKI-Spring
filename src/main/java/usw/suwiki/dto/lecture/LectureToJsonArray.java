package usw.suwiki.dto.lecture;

import lombok.Getter;

@Getter
public class LectureToJsonArray {
    Object data;

    Long count;

    public LectureToJsonArray(Object data, Long count) {
        this.data = data;
        this.count = count;
    }

}
