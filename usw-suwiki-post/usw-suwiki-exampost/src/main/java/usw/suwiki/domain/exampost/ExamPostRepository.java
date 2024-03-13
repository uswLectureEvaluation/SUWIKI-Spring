package usw.suwiki.domain.exampost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface ExamPostRepository extends JpaRepository<ExamPost, Long> {

    List<ExamPost> findAllByUser(User user);

    @Query(nativeQuery = true, value =
      "SELECT * FROM exam_post WHERE user_idx = :userIdx limit :defaultLimit offset :page"
    )
    List<ExamPost> findByUserIdxAndPagePotion(
            @Param("userIdx") Long userIdx,
            @Param("page") int page,
            @Param("defaultLimit") int defaultLimit
    );

    @Query(nativeQuery = true, value =
      "SELECT * FROM exam_post WHERE lecture_id = :lectureId limit :defaultLimit offset :page"
    )
    List<ExamPost> findAllByLectureId(
            @Param("lectureId") Long lectureId,
            @Param("page") int page,
            @Param("defaultLimit") int defaultLimit
    );

    Optional<ExamPost> findByUserAndLecture(User user, Lecture lecture);
}
