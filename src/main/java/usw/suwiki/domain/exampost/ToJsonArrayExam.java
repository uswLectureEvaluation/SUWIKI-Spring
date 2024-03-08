package usw.suwiki.domain.exampost;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ToJsonArrayExam {
    Object data;
    boolean isExamDataExist;

    public ToJsonArrayExam(Object data) {
        this.data = data;
        this.isExamDataExist = true;
    }
}
