package usw.suwiki.domain.lecture.schedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureScheduleQueryRepository {
  private final JPAQueryFactory queryFactory;

  public List<LectureSchedule> findAllSchedulesBySemesterContains(String semester) {
    return queryFactory.selectFrom(lectureSchedule)
      .join(lectureSchedule.lecture).fetchJoin()
      .where(lectureSchedule.lecture.semester.contains(semester))
      .fetch();
  }

  public List<LectureSchedule> findAllByLectureId(Long lectureId) {
    return queryFactory.selectFrom(lectureSchedule)
      .where(lectureSchedule.lectureId.eq(lectureId))
      .fetch();
  }
}
