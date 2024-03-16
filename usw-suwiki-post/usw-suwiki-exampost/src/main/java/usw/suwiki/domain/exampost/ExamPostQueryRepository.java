package usw.suwiki.domain.exampost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.exampost.dto.ExamPostResponse;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExamPostQueryRepository {
  private static final int LIMIT_PAGE_SIZE = 10;

  private final JPAQueryFactory queryFactory;

  public List<ExamPostResponse.MyPost> findAllByUserIdAndPageOption(Long userId, int page) {
    return queryFactory.select(new QExamPostResponse_MyPost(
        examPost.id,
        examPost.content,
        examPost.lectureName,
        examPost.selectedSemester,
        lecture.majorType,
        lecture.semesterList,
        examPost.examType,
        examPost.examInfo,
        examPost.examDifficulty))
      .from(examPost)
      .where(examPost.userId.eq(userId))
      .join(lecture).on(examPost.lectureId.eq(lecture.id))
      .limit(LIMIT_PAGE_SIZE)
      .offset(page)
      .fetch();
  }
}
