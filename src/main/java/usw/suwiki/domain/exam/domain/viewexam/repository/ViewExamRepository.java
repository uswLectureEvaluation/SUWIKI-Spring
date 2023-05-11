package usw.suwiki.domain.exam.domain.viewexam.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import usw.suwiki.domain.exam.domain.viewexam.ViewExam;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ViewExamRepository {
    void save(ViewExam viewExam);
    List<ViewExam> findByUserId(Long userIdx);
    void delete(ViewExam viewExam);
    boolean validateIsExists(Long userId, Long lectureId);
}