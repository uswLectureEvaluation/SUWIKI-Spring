package usw.suwiki.dto.evaluate;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvaluatePostsUpdateDto {

    private String semester;
    private Float satisfaction;    //수업 만족도
    private Float learning; //배움지수
    private Float honey;    //꿀강지수

    private int team;    //조모임 횟수
    private int difficulty;   //성적비율
    private int homework;

    private String content;    //주관적인 강의평가 입력내용

    public EvaluatePostsUpdateDto(String semester,Float satisfaction, Float learning, Float honey,
                                  int team, int difficulty, int homework, String content) {
        this.semester = semester;
        this.satisfaction = satisfaction;
        this.learning = learning;
        this.honey = honey;
        this.team = team;
        this.difficulty = difficulty;
        this.homework = homework;
        this.content = content;
    }
}
