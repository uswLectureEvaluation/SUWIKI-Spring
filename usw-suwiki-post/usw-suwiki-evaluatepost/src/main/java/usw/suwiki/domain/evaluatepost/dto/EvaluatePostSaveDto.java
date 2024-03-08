package usw.suwiki.domain.evaluatepost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EvaluatePostSaveDto {
    private String lectureName;
    private String selectedSemester;
    private String professor;
    private float satisfaction;
    private float learning;
    private float honey;
    private int team;
    private int difficulty;
    private int homework;
    private String content;
}
