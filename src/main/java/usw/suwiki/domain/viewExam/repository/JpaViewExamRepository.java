package usw.suwiki.domain.viewExam.repository;

import org.springframework.stereotype.Repository;
import usw.suwiki.domain.viewExam.entity.ViewExam;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JpaViewExamRepository implements ViewExamRepository {

    private final EntityManager em;

    public JpaViewExamRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(ViewExam viewExam) {
        em.persist(viewExam);
    }

    @Override
    public boolean validateIsExists(Long userId, Long lectureId) {
        return em.createQuery("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END "
                + "FROM ViewExam v "
                + "WHERE v.user.id = :userId and v.lecture.id = :lectureId", Boolean.class)
            .setParameter("userId", userId)
            .setParameter("lectureId", lectureId)
            .getSingleResult();
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
