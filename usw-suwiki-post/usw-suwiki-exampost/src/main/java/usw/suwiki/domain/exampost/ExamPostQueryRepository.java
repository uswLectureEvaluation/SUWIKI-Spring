package usw.suwiki.domain.exampost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.exampost.dto.ExamPostResponse;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExamPostQueryRepository {
  private final JPAQueryFactory queryFactory;

  public List<ExamPostResponse.MyPost> findByUserIdxAndPageOption(Long userId, int page, int limit) {
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
      .limit(limit)
      .offset(page)
      .fetch();
  }
}
