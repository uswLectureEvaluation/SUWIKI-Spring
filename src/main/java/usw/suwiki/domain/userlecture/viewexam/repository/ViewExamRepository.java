package usw.suwiki.domain.userlecture.viewexam.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.userlecture.viewexam.ViewExam;

@Repository
@Transactional(readOnly = true)
public interface ViewExamRepository {

    void save(ViewExam viewExam);

    List<ViewExam> findByUserId(Long userIdx);

    void delete(ViewExam viewExam);

    boolean validateIsExists(Long userId, Long lectureId);
}