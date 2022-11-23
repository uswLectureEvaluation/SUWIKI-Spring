package usw.suwiki.domain.evaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;

@Getter
@NoArgsConstructor
public class EvaluatePostsToLecture {

    private Long lectureId;
    private float lectureTotal;
    private float lectureSatisfaction;
    private float lectureHoney;
    private float lectureLearning;
    private float lectureTeam;
    private float lectureDifficulty;
    private float lectureHomework;

    public EvaluatePostsToLecture(EvaluatePosts posts) {
        this.lectureId = posts.getLecture().getId();
        this.lectureTotal = posts.getTotalAvg();
        this.lectureSatisfaction = posts.getSatisfaction();
        this.lectureHoney = posts.getHoney();
        this.lectureLearning = posts.getLearning();
        this.lectureTeam = posts.getTeam();
        this.lectureDifficulty = posts.getDifficulty();
        this.lectureHomework = posts.getHomework();
    }
}
