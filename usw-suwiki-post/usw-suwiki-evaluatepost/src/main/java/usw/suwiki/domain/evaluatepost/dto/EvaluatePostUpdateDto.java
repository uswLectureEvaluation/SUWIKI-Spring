package usw.suwiki.domain.evaluatepost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EvaluatePostUpdateDto {
    private String selectedSemester;
    private float satisfaction;
    private float learning;
    private float honey;
    private int team;
    private int difficulty;
    private int homework;
    private String content;
}
