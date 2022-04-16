package usw.suwiki.repository.viewExam;

import org.springframework.stereotype.Repository;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.viewExam.ViewExam;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JpaViewExamRepository implements ViewExamRepository{

    private final EntityManager em;

    public JpaViewExamRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(ViewExam viewExam) {
        em.persist(viewExam);
    }

    @Override
    public List<ViewExam> findByUserId(Long userIdx) {
        List<ViewExam> resultList = em.createQuery("SELECT v FROM ViewExam v JOIN v.user u WHERE u.id = :idx ORDER BY v.createDate")
                .setParameter("idx", userIdx)
                .getResultList();     //refactoring 해야한다
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        } else return resultList;
    }

    @Override
    public void delete(ViewExam viewExam) {
        em.remove(viewExam);
    }
}
