package usw.suwiki.domain.lecture.data;

/**
 * EvaluatePost를 작성함으로 평가된 데이터 정보들로, 모듈 간의 데이터를 넘겨주는 용도로 사용된다.
 *
 * @author hejow
 */
public record EvaluatedData(
  float totalAverage,
  float satisfaction,
  float honey,
  float learning,
  float team,
  float difficulty,
  float homework
) {
}
