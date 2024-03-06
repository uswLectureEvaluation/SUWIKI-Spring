package usw.suwiki.domain.evaluatepost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindByLectureToJson {
    private Object data;
    private boolean isWritten;

    public FindByLectureToJson(Object data) {
        this.data = data;
        this.isWritten = true;
    }

    public void setWritten(boolean isWritten) {
        this.isWritten = isWritten;
    }
}
