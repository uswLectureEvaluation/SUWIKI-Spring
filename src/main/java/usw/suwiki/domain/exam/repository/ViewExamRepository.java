package usw.suwiki.domain.exam.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.viewExam.entity.ViewExam;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ViewExamRepository {

    void save(ViewExam viewExam);

    List<ViewExam> findByUserId(Long userIdx);

    void delete(ViewExam viewExam);
}