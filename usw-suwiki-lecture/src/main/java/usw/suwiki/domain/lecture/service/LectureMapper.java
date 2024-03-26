package usw.suwiki.domain.lecture.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.dto.LectureResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class LectureMapper {

  public static LectureResponse.Simple toSimple(Lecture lecture) {
    return new LectureResponse.Simple(
      lecture.getId(),
      lecture.getSemester(),
      lecture.getProfessor(),
      lecture.getType(),
      lecture.getName(),
      lecture.getMajorType(),
      lecture.getLectureEvaluationInfo().getLectureTotalAvg(),
      lecture.getLectureEvaluationInfo().getLectureSatisfactionAvg(),
      lecture.getLectureEvaluationInfo().getLectureHoneyAvg(),
      lecture.getLectureEvaluationInfo().getLectureLearningAvg()
    );
  }

  public static LectureResponse.Detail toDetail(Lecture lecture) {
    return new LectureResponse.Detail(
      lecture.getId(),
      lecture.getSemester(),
      lecture.getProfessor(),
      lecture.getType(),
      lecture.getName(),
      lecture.getMajorType(),
      lecture.getLectureEvaluationInfo().getLectureTotalAvg(),
      lecture.getLectureEvaluationInfo().getLectureSatisfactionAvg(),
      lecture.getLectureEvaluationInfo().getLectureHoneyAvg(),
      lecture.getLectureEvaluationInfo().getLectureLearningAvg(),
      lecture.getLectureEvaluationInfo().getLectureTeamAvg(),
      lecture.getLectureEvaluationInfo().getLectureDifficultyAvg(),
      lecture.getLectureEvaluationInfo().getLectureHomeworkAvg()
    );
  }
}
