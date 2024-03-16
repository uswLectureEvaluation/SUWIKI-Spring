package usw.suwiki.domain.viewexam;

import java.util.List;

public interface ViewExamRepository {

  void save(ViewExam viewExam);

  List<ViewExam> findByUserId(Long userIdx);

  void delete(ViewExam viewExam);

  boolean validateIsExists(Long userId, Long lectureId);
}
