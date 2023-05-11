package usw.suwiki.domain.evaluation.controller.dto;

import lombok.Getter;
import usw.suwiki.domain.evaluation.domain.EvaluatePosts;

@Getter
public class EvaluateResponseByUserIdxDto {

    private Long id;
    private String lectureName;
    private String professor;
    private String majorType;
    private String selectedSemester;
    private String semesterList;
    private float totalAvg;   // 평균지수
    private float satisfaction;    //수업 만족도
    private float learning; //배움지수
    private float honey;    //꿀강지수

    private int team;    //조모임 횟수
    private int difficulty;   //학점비율
    private int homework;

    private String content;    //주관적인 강의평가 입력내용

    public void setSemesterList(String semesterList) {
        this.semesterList = semesterList;
    }

    public EvaluateResponseByUserIdxDto(EvaluatePosts entity) {
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
