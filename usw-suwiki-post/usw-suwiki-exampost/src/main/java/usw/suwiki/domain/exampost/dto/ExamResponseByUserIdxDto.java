package usw.suwiki.domain.exampost.dto;

import lombok.Getter;
import lombok.Setter;
import usw.suwiki.domain.exampost.ExamPost;

@Getter
public class ExamResponseByUserIdxDto {

    private final Long id;
    private final String lectureName;
    private final String professor;
    private final String majorType;
    private final String selectedSemester;

    @Setter
    private String semesterList;

    private final String examType;
    private final String examInfo;
    private final String examDifficulty;
    private final String content;

    public ExamResponseByUserIdxDto(ExamPost entity) {
        this.id = entity.getId();
        this.lectureName = entity.getLectureName();
        this.majorType = entity.getLecture().getMajorType();
        this.selectedSemester = entity.getSelectedSemester();
        this.professor = entity.getProfessor();
        this.examType = entity.getExamType();
        this.examInfo = entity.getExamInfo();
        this.examDifficulty = entity.getExamDifficulty();
        this.content = entity.getContent();
    }

}
