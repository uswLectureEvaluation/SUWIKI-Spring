package usw.suwiki.domain.evaluatepost.controller.dto;

import lombok.Getter;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;

@Getter
public class EvaluatePostResponseByUserIdxDto {

    private final Long id;
    private final String lectureName;
    private final String professor;
    private final String majorType;
    private final String selectedSemester;
    private String semesterList;
    private final float totalAvg;   // 평균지수
    private final float satisfaction;    //수업 만족도
    private final float learning; //배움지수
    private final float honey;    //꿀강지수

    private final int team;    //조모임 횟수
    private final int difficulty;   //학점비율
    private final int homework;

    private final String content;    //주관적인 강의평가 입력내용

    public void setSemesterList(String semesterList) {
        this.semesterList = semesterList;
    }

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
