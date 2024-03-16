package usw.suwiki.domain.lecture;

import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.data.EvaluatedData;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
public class LectureEvaluationInfo {
  private static final int EVALUATION_TYPE_COUNT = 3;
  private static final int ZERO_POST = 0;

  private float lectureTotalAvg;
  private float lectureSatisfactionAvg;
  private float lectureHoneyAvg;
  private float lectureLearningAvg;
  private float lectureTeamAvg;
  private float lectureDifficultyAvg;
  private float lectureHomeworkAvg;
  private float lectureSatisfactionValue;
  private float lectureHoneyValue;
  private float lectureLearningValue;
  private float lectureTeamValue;
  private float lectureDifficultyValue;
  private float lectureHomeworkValue;

  public void calculateAverage(int postsCount) {
    this.lectureSatisfactionAvg = average(this.lectureSatisfactionValue, postsCount);
    this.lectureHoneyAvg = average(this.lectureHoneyValue, postsCount);
    this.lectureLearningAvg = average(this.lectureLearningValue, postsCount);
    this.lectureTeamAvg = average(this.lectureTeamValue, postsCount);
    this.lectureDifficultyAvg = average(this.lectureDifficultyValue, postsCount);
    this.lectureHomeworkAvg = average(this.lectureHomeworkValue, postsCount);
    this.lectureTotalAvg = (lectureSatisfactionAvg + lectureHoneyAvg + lectureLearningAvg) / EVALUATION_TYPE_COUNT;
  }

  public void apply(EvaluatedData data) {
    this.lectureSatisfactionValue += data.satisfaction();
    this.lectureHoneyValue += data.honey();
    this.lectureLearningValue += data.learning();
    this.lectureTeamValue += data.team();
    this.lectureDifficultyValue += data.difficulty();
    this.lectureHomeworkValue += data.homework();
  }

  public void cancel(EvaluatedData data) {
    this.lectureSatisfactionValue -= data.satisfaction();
    this.lectureHoneyValue -= data.honey();
    this.lectureLearningValue -= data.learning();
    this.lectureTeamValue -= data.team();
    this.lectureDifficultyValue -= data.difficulty();
    this.lectureHomeworkValue -= data.homework();
  }

  private float average(float amount, int count) {
    return count == ZERO_POST ? 0.0f : amount / count;
  }
}
