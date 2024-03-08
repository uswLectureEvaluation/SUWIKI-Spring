package usw.suwiki.domain.exampost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExamPostsSaveDto {
    private String lectureName;
    private String selectedSemester;
    private String professor;
    private String examType;
    private String examInfo;
    private String examDifficulty;
    private String content;
}
