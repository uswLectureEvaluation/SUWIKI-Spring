package usw.suwiki.domain.evaluatepost.dto;

import lombok.Getter;
import lombok.Setter;
import usw.suwiki.domain.evaluatepost.EvaluatePost;

@Getter
public class EvaluatePostResponseByUserIdxDto {

    private final Long id;
    private final String lectureName;
    private final String professor;
    private final String majorType;
    private final String selectedSemester;

    @Setter
    private String semesterList;

    private final float totalAvg;
    private final float satisfaction;
    private final float learning;
    private final float honey;
    private final int team;
    private final int difficulty;
    private final int homework;

    private final String content;

    public EvaluatePostResponseByUserIdxDto(EvaluatePost entity) {
        this.id = entity.getId();
        this.lectureName = entity.getLectureName();
        this.professor = entity.getProfessor();
        this.majorType = entity.getLecture().getMajorType();
        this.selectedSemester = entity.getSelectedSemester();
        this.totalAvg = entity.getTotalAvg();
        this.satisfaction = entity.getSatisfaction();
        this.learning = entity.getLearning();
        this.honey = entity.getHoney();
        this.team = entity.getTeam();
        this.difficulty = entity.getDifficulty();
        this.homework = entity.getHomework();
        this.content = entity.getContent();
    }
}
