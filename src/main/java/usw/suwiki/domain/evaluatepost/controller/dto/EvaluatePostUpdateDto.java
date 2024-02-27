package usw.suwiki.domain.evaluatepost.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvaluatePostUpdateDto {

    private String selectedSemester;
    private float satisfaction;
    private float learning;
    private float honey;
    private int team;
    private int difficulty;
    private int homework;
    private String content;

    public EvaluatePostUpdateDto(
        String selectedSemester,
        float satisfaction,
        float learning,
        float honey,
        int team,
        int difficulty,
        int homework,
        String content
    ) {
        this.selectedSemester = selectedSemester;
        this.satisfaction = satisfaction;
        this.learning = learning;
        this.honey = honey;
        this.team = team;
        this.difficulty = difficulty;
        this.homework = homework;
        this.content = content;
    }
}
