package usw.suwiki.domain.exampost.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExamPostsSaveDto {

    private String lectureName;
    private String selectedSemester;
    private String professor;
    private String examType;
    private String examInfo;
    private String examDifficulty;
    private String content;

    public ExamPostsSaveDto(String lectureName, String selectedSemester, String professor, String examType,
        String examInfo,
        String examDifficulty, String content) {
        this.lectureName = lectureName;
        this.selectedSemester = selectedSemester;
        this.professor = professor;
        this.examType = examType;
        this.examInfo = examInfo;
        this.examDifficulty = examDifficulty;
        this.content = content;
    }
}
