package usw.suwiki.domain.lecture.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

import lombok.NoArgsConstructor;
import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;

@Embeddable
@Access(AccessType.FIELD)
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
		final int EVALUATION_TYPE_COUNT = 3;
		this.lectureSatisfactionAvg = calculateAvg(this.lectureSatisfactionValue, postsCount);
		this.lectureHoneyAvg = calculateAvg(this.lectureHoneyValue, postsCount);
		this.lectureLearningAvg = calculateAvg(this.lectureLearningValue, postsCount);
		this.lectureTeamAvg = calculateAvg(this.lectureTeamValue, postsCount);
		this.lectureDifficultyAvg = calculateAvg(this.lectureDifficultyValue, postsCount);
		this.lectureHomeworkAvg = calculateAvg(this.lectureHomeworkValue, postsCount);
		this.lectureTotalAvg = (lectureSatisfactionAvg + lectureHoneyAvg + lectureLearningAvg) / EVALUATION_TYPE_COUNT;
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

	public float getLectureTotalAvg() {
		return this.lectureTotalAvg;
	}

	public float getLectureSatisfactionAvg() {
		return this.lectureSatisfactionAvg;
	}

	public float getLectureHoneyAvg() {
		return this.lectureHoneyAvg;
	}

	public float getLectureLearningAvg() {
		return this.lectureLearningAvg;
	}

	public float getLectureTeamAvg() {
		return this.lectureTeamAvg;
	}

	public float getLectureDifficultyAvg() {
		return this.lectureDifficultyAvg;
	}

	public float getLectureHomeworkAvg() {
		return this.lectureHomeworkAvg;
	}

	public float calculateAvg(float amountValue, int count) {
		return amountValue / count;
	}
}
