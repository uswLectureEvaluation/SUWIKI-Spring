package usw.suwiki.domain.evaluatepost.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
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

    @Builder
    public EvaluatePostSaveDto(
        String lectureName,
        String selectedSemester,
        String professor,
        float satisfaction,
        float learning,
        float honey,
        int team,
        int difficulty,
        int homework,
        String content
    ) {
        this.lectureName = lectureName;
        this.selectedSemester = selectedSemester;
        this.professor = professor;
        this.satisfaction = satisfaction;
        this.learning = learning;
        this.honey = honey;
        this.team = team;
        this.difficulty = difficulty;
        this.homework = homework;
        this.content = content;
    }
}
