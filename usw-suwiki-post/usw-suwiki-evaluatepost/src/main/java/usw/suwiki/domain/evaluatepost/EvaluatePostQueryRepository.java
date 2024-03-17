package usw.suwiki.domain.evaluatepost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponse;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvaluatePostQueryRepository {
  private static final int LIMIT_PAGE_SIZE = 10;

  private final JPAQueryFactory queryFactory;

  public List<EvaluatePostResponse.Detail> findAllByLectureIdAndPageOption(Long lectureId, int page) {
    return queryFactory.select(new QEvaludatePostResponse_Detail(
        evaluatePost.id,
        evaluatePost.content,
        evaluatePost.selectedSemester,
        evaluatePost.totalAvg,
        evaluatePost.satisfaction,
        evaluatePost.learning,
        evaluatePost.honey,
        evaluatePost.team,
        evaluatePost.difficulty,
        evaluatePost.homework))
      .from(evaluatePost)
      .where(evaluatePost.lectureId.eq(lectureId))
      .limit(LIMIT_PAGE_SIZE)
      .offset(page)
      .fetch();
  }

  public List<EvaluatePostResponse.MyPost> findAllByUserIdAndPageOption(Long userId, int page) {
    return queryFactory.select(new QEvaludatePostResponse_MyPost(
        evaluatePost.id,
        evaluatePost.content,
        evaluatePost.lectureName,
        evaluatePost.selectedSemester,
        evaluatePost.professor,
        lecture.majorType,
        lecture.semesterList,
        evaluatePost.totalAvg,
        evaluatePost.satisfaction,
        evaluatePost.learning,
        evaluatePost.honey,
        evaluatePost.team,
        evaluatePost.difficulty,
        evaluatePost.homework))
      .from(evaluatePost)
      .join(lecture).on(evaluatePost.lectureId.eq(lecture.id))
      .where(evaluatePost.userId.eq(userId))
      .limit(LIMIT_PAGE_SIZE)
      .offset(page)
      .fetch();
  }
}
