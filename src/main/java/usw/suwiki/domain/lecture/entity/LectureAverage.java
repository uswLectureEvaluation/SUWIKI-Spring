package usw.suwiki.domain.lecture.entity;

import javax.persistence.Embeddable;

import lombok.NoArgsConstructor;
import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;

@Embeddable
@NoArgsConstructor
public class LectureAverage {
	private float lectureTotalAvg = 0;
	private float lectureSatisfactionAvg = 0;
	private float lectureHoneyAvg = 0;
	private float lectureLearningAvg = 0;
	private float lectureTeamAvg = 0;
	private float lectureDifficultyAvg = 0;
	private float lectureHomeworkAvg = 0;

	private float lectureSatisfactionValue = 0;
	private float lectureHoneyValue = 0;
	private float lectureLearningValue = 0;
	private float lectureTeamValue = 0;
	private float lectureDifficultyValue = 0;
	private float lectureHomeworkValue = 0;

	public void calculateLectureAverage(int postsCount) {
		this.lectureSatisfactionAvg = calculateAvg(this.lectureSatisfactionValue, postsCount);
		this.lectureHoneyAvg = calculateAvg(this.lectureHoneyValue, postsCount);
		this.lectureLearningAvg = calculateAvg(this.lectureLearningValue, postsCount);
		this.lectureTeamAvg = calculateAvg(this.lectureTeamValue, postsCount);
		this.lectureDifficultyAvg = calculateAvg(this.lectureDifficultyValue, postsCount);
		this.lectureHomeworkAvg = calculateAvg(this.lectureHomeworkValue, postsCount);
		this.lectureTotalAvg = (lectureSatisfactionAvg + lectureHoneyAvg + lectureLearningAvg) / 3;
	}

	public void calculateIfPostCountLessThanOne() {
		this.lectureTotalAvg = 0;
		this.lectureSatisfactionAvg = 0;
		this.lectureHoneyAvg = 0;
		this.lectureLearningAvg = 0;
		this.lectureTeamAvg = 0;
		this.lectureDifficultyAvg = 0;
		this.lectureHomeworkAvg = 0;
	}

	public void addLectureValue(EvaluatePostsToLecture dto) {
		this.lectureSatisfactionValue += dto.getLectureSatisfaction();
		this.lectureHoneyValue += dto.getLectureHoney();
		this.lectureLearningValue += dto.getLectureLearning();
		this.lectureTeamValue += dto.getLectureTeam();
		this.lectureDifficultyValue += dto.getLectureDifficulty();
		this.lectureHomeworkValue += dto.getLectureHomework();
	}

	public void cancelLectureValue(EvaluatePostsToLecture dto) {
		this.lectureSatisfactionValue -= dto.getLectureSatisfaction();
		this.lectureHoneyValue -= dto.getLectureHoney();
		this.lectureLearningValue -= dto.getLectureLearning();
		this.lectureTeamValue -= dto.getLectureTeam();
		this.lectureDifficultyValue -= dto.getLectureDifficulty();
		this.lectureHomeworkValue -= dto.getLectureHomework();
	}

	public float calculateAvg(float amountValue, int count) {
		return amountValue / count;
	}
}
