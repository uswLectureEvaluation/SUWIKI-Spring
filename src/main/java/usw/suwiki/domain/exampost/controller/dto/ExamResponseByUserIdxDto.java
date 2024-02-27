package usw.suwiki.domain.exampost.controller.dto;

import lombok.Getter;
import usw.suwiki.domain.exampost.domain.ExamPost;

@Getter
public class ExamResponseByUserIdxDto {

    private final Long id;
    private final String lectureName;
    private final String professor;
    private final String majorType;
    private final String selectedSemester;
    private String semesterList;
    private final String examType;
    private final String examInfo;
    private final String examDifficulty;
    private final String content;

    public void setSemesterList(String semesterList) {
        this.semesterList = semesterList;
    }

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
