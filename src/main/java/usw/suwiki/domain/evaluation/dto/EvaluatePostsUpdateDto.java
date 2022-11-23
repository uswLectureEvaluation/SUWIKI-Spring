package usw.suwiki.domain.evaluation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvaluatePostsUpdateDto {

    private String selectedSemester;
    private float satisfaction;    //수업 만족도
    private float learning; //배움지수
    private float honey;    //꿀강지수

    private int team;    //조모임 횟수
    private int difficulty;   //성적비율
    private int homework;

    private String content;    //주관적인 강의평가 입력내용

    public EvaluatePostsUpdateDto(String selectedSemester, float satisfaction, float learning, float honey,
                                  int team, int difficulty, int homework, String content) {
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
