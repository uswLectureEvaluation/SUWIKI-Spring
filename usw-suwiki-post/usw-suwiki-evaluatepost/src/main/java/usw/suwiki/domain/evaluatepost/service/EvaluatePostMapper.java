package usw.suwiki.domain.evaluatepost.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.evaluatepost.LectureInfo;
import usw.suwiki.domain.evaluatepost.LectureRating;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostRequest;
import usw.suwiki.domain.lecture.model.Evaluation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class EvaluatePostMapper {

  public static EvaluatePost toEntity(Long userId, Long lectureId, EvaluatePostRequest.Create request) {
    return new EvaluatePost(
      userId,
      request.getContent(),
      new LectureInfo(lectureId, request.getLectureName(), request.getSelectedSemester(), request.getProfessor()),
      new LectureRating(request.getSatisfaction(), request.getLearning(), request.getHoney(), request.getTeam(), request.getDifficulty(), request.getHomework())
    );
  }

  public static LectureRating toRating(EvaluatePostRequest.Update request) {
    return new LectureRating(request.getSatisfaction(), request.getLearning(), request.getHoney(), request.getTeam(), request.getDifficulty(), request.getHomework());
  }

  public static Evaluation toEvaluatedData(LectureRating lectureRating) {
    return new Evaluation(
      lectureRating.getTotalAvg(),
      lectureRating.getSatisfaction(),
      lectureRating.getHoney(),
      lectureRating.getLearning(),
      lectureRating.getTeam(),
      lectureRating.getDifficulty(),
      lectureRating.getHomework()
    );
  }
}
