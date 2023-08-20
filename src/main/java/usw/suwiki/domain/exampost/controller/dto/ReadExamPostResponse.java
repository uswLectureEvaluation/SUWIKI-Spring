package usw.suwiki.domain.exampost.controller.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReadExamPostResponse {
    Object data;

    boolean canRead;
    boolean isExamDataExist;
    boolean isWritten = true;

    public ReadExamPostResponse(Object data) {
        this.data = data;
        this.isExamDataExist = true;
    }

    public static ReadExamPostResponse hasExamPost(List<ExamResponseByLectureIdDto> data, boolean isWrite) {
        ReadExamPostResponse response = new ReadExamPostResponse(data);
        response.isExamDataExist = Boolean.TRUE;
        response.isWritten = isWrite;
        response.canRead = Boolean.TRUE;
        return response;
    }

    public static ReadExamPostResponse hasNotExamPost(boolean isWrite) {
        ReadExamPostResponse response = new ReadExamPostResponse(new ArrayList<>());
        response.isExamDataExist = Boolean.FALSE;
        response.isWritten = isWrite;
        response.canRead = Boolean.TRUE;
        return response;
    }

    public void  forbiddenToRead() {
        this.data = new ArrayList<>();
        this.canRead = Boolean.FALSE;
    }
}
