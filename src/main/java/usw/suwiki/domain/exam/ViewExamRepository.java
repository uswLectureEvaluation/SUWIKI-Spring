package usw.suwiki.domain.exam;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.viewExam.ViewExam;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ViewExamRepository{

    public void save(ViewExam viewExam);

    List<ViewExam> findByUserId(Long userIdx);

    public void delete(ViewExam viewExam);
}