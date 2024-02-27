package usw.suwiki.domain.exampost.controller.dto;

import lombok.Getter;
import usw.suwiki.domain.exampost.domain.ExamPost;

@Getter
public class ExamResponseByLectureIdDto {

    private final Long id;
    private final String selectedSemester;
    private final String examType;
    private final String examInfo;
    private final String examDifficulty;
    private final String content;

    public ExamResponseByLectureIdDto(ExamPost entity) {
        this.id = entity.getId();
        this.selectedSemester = entity.getSelectedSemester();
        this.examType = entity.getExamType();
        this.examInfo = entity.getExamInfo();
        this.examDifficulty = entity.getExamDifficulty();
        this.content = entity.getContent();
    }

}
