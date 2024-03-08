package usw.suwiki.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LectureAndCountResponseForm {
    Object data;
    Long count;
}
