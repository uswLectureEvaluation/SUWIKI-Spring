package usw.suwiki.domain.evaluation.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class EvaluatePostsSaveDto {

    private String lectureName; //과목
    private String selectedSemester;
    private String professor;   //교수

    private float satisfaction;    //수업 만족도
    private float learning; //배움지수
    private float honey;    //꿀강지수

    private int team;    //조모임 횟수
    private int difficulty;   //성적비율
    private int homework;

    private String content;    //주관적인 강의평가 입력내용

    @Builder
    public EvaluatePostsSaveDto(String lectureName, String selectedSemester, String professor, float satisfaction,
                                float learning, float honey, int team, int difficulty, int homework, String content) {
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
