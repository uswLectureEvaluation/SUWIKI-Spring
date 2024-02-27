package usw.suwiki.domain.lecture.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureAndCountResponseForm {

    Object data;

    Long count;

    public LectureAndCountResponseForm(Object data, Long count) {
        this.data = data;
        this.count = count;
    }
}
