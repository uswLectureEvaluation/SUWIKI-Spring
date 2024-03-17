package usw.suwiki.domain.evaluatepost;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluatePostRepository extends JpaRepository<EvaluatePost, Long> {

  List<EvaluatePost> findAllByUserId(Long userId);

  // todo: must test
  boolean existsByUserIdAndLectureInfoLectureId(Long userId, Long lectureId);
}
