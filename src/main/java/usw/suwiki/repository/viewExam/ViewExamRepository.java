package usw.suwiki.repository.viewExam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.viewExam.ViewExam;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ViewExamRepository{

    public void save(ViewExam viewExam);

    List<ViewExam> findByUserId(Long userIdx);

}