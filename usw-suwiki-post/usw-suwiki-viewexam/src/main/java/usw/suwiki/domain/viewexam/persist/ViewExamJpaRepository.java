package usw.suwiki.domain.viewexam.persist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.viewexam.ViewExam;
import usw.suwiki.domain.viewexam.ViewExamRepository;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
class ViewExamJpaRepository implements ViewExamRepository {
  private final EntityManager em;

  @Override
  public void save(ViewExam viewExam) {
    em.persist(viewExam);
  }

  @Override
  public boolean validateIsExists(Long userId, Long lectureId) {
    return em.createQuery("""
            SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
            FROM ViewExam v
            WHERE v.user.id = :userId and v.lecture.id = :lectureId
        """, Boolean.class)
      .setParameter("userId", userId)
      .setParameter("lectureId", lectureId)
      .getSingleResult();
  }

  @Override
  public List<ViewExam> findByUserId(Long userIdx) {
    List result = em.createQuery("SELECT v FROM ViewExam v JOIN v.user u WHERE u.id = :idx ORDER BY v.createDate")
      .setParameter("idx", userIdx)
      .getResultList();

    return result.isEmpty() ? Collections.emptyList() : result;
  }

  @Override
  public void delete(ViewExam viewExam) {
    em.remove(viewExam);
  }
}
