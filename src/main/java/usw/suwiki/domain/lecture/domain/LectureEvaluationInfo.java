package usw.suwiki.domain.lecture.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

import usw.suwiki.domain.evaluatepost.service.dto.EvaluatePostsToLecture;

@Embeddable
@Access(AccessType.FIELD)
public class LectureEvaluationInfo {
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

	public LectureEvaluationInfo() {
		this.lectureTotalAvg = 0.0f;
		this.lectureSatisfactionAvg = 0.0f;
		this.lectureHoneyAvg = 0.0f;
		this.lectureLearningAvg = 0.0f;
		this.lectureTeamAvg = 0.0f;
		this.lectureDifficultyAvg = 0.0f;
		this.lectureHomeworkAvg = 0.0f;
		this.lectureSatisfactionValue = 0.0f;
		this.lectureHoneyValue = 0.0f;
		this. lectureLearningValue = 0.0f;
		this.lectureTeamValue = 0.0f;
		this.lectureDifficultyValue = 0.0f;
		this.lectureHomeworkValue = 0.0f;
	}

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
		this.lectureTotalAvg = 0.0f;
		this.lectureSatisfactionAvg = 0.0f;
		this.lectureHoneyAvg = 0.0f;
		this.lectureLearningAvg = 0.0f;
		this.lectureTeamAvg = 0.0f;
		this.lectureDifficultyAvg = 0.0f;
		this.lectureHomeworkAvg = 0.0f;
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
