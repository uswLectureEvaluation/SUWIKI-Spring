package usw.suwiki.domain.evaluation.service.dto;

import lombok.Getter;

@Getter
public class FindByLectureToJson {
    Object data;
    boolean isWritten = true;

    public FindByLectureToJson(Object data) {
        this.data = data;
    }

    public void setWritten(boolean written) {
        isWritten = written;
    }
}
