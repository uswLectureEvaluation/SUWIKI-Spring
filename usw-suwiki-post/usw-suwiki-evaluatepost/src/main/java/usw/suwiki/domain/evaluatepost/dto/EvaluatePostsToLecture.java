package usw.suwiki.domain.evaluatepost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.evaluatepost.EvaluatePost;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluatePostsToLecture {

    private Long lectureId;
    private float lectureTotal;
    private float lectureSatisfaction;
    private float lectureHoney;
    private float lectureLearning;
    private float lectureTeam;
    private float lectureDifficulty;
    private float lectureHomework;

    public EvaluatePostsToLecture(EvaluatePost posts) {
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
