package usw.suwiki.dto.exam_info;

import lombok.Getter;
import usw.suwiki.domain.exam.ExamPosts;

@Getter
public class ExamResponseByUserIdxDto {

    private Long id;
    private String lectureName;
    private String professor;
    private String semester;
    private String semesterList;
    private String examInfo;    
    private String examDifficulty;    //난이도

    private String content;    //주관적인 강의평가 입력내용

    public void setSemesterList(String semesterList) {
        this.semesterList = semesterList;
    }

    public ExamResponseByUserIdxDto(ExamPosts entity) {
        this.id = entity.getId();
        this.lectureName = entity.getLectureName();
        this.semester = entity.getSemester();
        this.professor = entity.getProfessor();
        this.examInfo = entity.getExamInfo();
        this.examDifficulty = entity.getExamDifficulty();
        this.content = entity.getContent();
    }

}
