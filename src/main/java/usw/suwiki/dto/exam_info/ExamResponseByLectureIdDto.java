package usw.suwiki.dto.exam_info;

import usw.suwiki.domain.exam.ExamPosts;
import lombok.Getter;

@Getter
public class ExamResponseByLectureIdDto {

    private Long id;
    private String selectedSemester;

    private String examType;
    private String examInfo;    //시험방식
    private String examDifficulty;    //난이도

    private String content;    //주관적인 강의평가 입력내용

    public ExamResponseByLectureIdDto(ExamPosts entity) {
        this.id = entity.getId();
        this.selectedSemester = entity.getSelectedSemester();
        this.examType = entity.getExamType();
        this.examInfo = entity.getExamInfo();
        this.examDifficulty = entity.getExamDifficulty();
        this.content = entity.getContent();
    }

}
