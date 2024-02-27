package usw.suwiki.domain.exampost.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExamPostUpdateDto {

    private String selectedSemester;
    private String examType;
    private String examInfo;
    private String examDifficulty;
    private String content;

    public ExamPostUpdateDto(String selectedSemester, String examType, String examInfo, String examDifficulty,
        String content) {
        this.selectedSemester = selectedSemester;
        this.examType = examType;
        this.examInfo = examInfo;
        this.examDifficulty = examDifficulty;
        this.content = content;
    }
}
