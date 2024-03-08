package usw.suwiki.domain.exampost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExamPostUpdateDto {
    private String selectedSemester;
    private String examType;
    private String examInfo;
    private String examDifficulty;
    private String content;
}
