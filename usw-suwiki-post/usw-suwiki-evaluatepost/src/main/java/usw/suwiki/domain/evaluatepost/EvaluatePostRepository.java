package usw.suwiki.domain.evaluatepost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import usw.suwiki.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface EvaluatePostRepository extends JpaRepository<EvaluatePost, Long> {

    List<EvaluatePost> findAllByUser(User user);

    @Query(nativeQuery = true, value =
      "SELECT * FROM evaluate_post WHERE user_idx = :userIdx limit :defaultLimit offset :page"
    )
    List<EvaluatePost> findByUserIdxAndPagePotion(
      @Param("userIdx") Long userIdx,
      @Param("page") int page,
      @Param("defaultLimit") int defaultLimit
    );

    @Query(nativeQuery = true, value =
      "SELECT * FROM evaluate_post WHERE lecture_id = :lectureId limit :defaultLimit offset :page"
    )
    List<EvaluatePost> findAllByLectureIdAndPageOption(
            @Param("lectureId") Long lectureId,
            @Param("page") int page,
            @Param("defaultLimit") int defaultLimit
    );

    Optional<EvaluatePost> findByUserAndLecture(User user, Lecture lecture);
}
