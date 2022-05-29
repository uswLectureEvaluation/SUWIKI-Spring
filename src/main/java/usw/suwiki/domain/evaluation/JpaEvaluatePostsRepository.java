package usw.suwiki.domain.evaluation;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.global.PageOption;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaEvaluatePostsRepository implements EvaluatePostsRepository {

    private final EntityManager em;

    public JpaEvaluatePostsRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(EvaluatePosts EvaluatePosts) {
        em.persist(EvaluatePosts);
    }

    @Override
    public EvaluatePosts findById(Long id) {
        EvaluatePosts posts = em.find(EvaluatePosts.class, id);
        return posts;
    }

    @Override
    public List<EvaluatePosts> findByLectureId(PageOption option, Long lectureId) {
        Optional<Integer> page = option.getPageNumber();
        if(page.isEmpty()){
            page = Optional.of(1);
        }

        List resultList = em.createQuery("SELECT p from EvaluatePosts p join p.lecture l WHERE l.id = :lectureId ORDER BY p.modifiedDate DESC")
                .setParameter("lectureId", lectureId)
                .setFirstResult((page.get()-1)*10)
                .setMaxResults(10)
                .getResultList();

        return resultList;
    }

    @Override
    public List<EvaluatePosts> findByUserId(PageOption option, Long userId) {
        Optional<Integer> page = option.getPageNumber();
        if(page.isEmpty()){
            page = Optional.of(1);
        }

        List resultList = em.createQuery("SELECT p from EvaluatePosts p join p.user u WHERE u.id = :id ORDER BY p.modifiedDate DESC")
                .setParameter("id", userId)
                .setFirstResult((page.get()-1)*10)
                .setMaxResults(10)
                .getResultList();

        return resultList;
    }

    @Override
    public boolean verifyPostsByIdx(User user, Lecture lecture) {
        List resultList = em.createQuery("SELECT p from EvaluatePosts p WHERE p.user = :user AND p.lecture = :lecture")
                .setParameter("user", user)
                .setParameter("lecture", lecture)
                .getResultList();
        if(resultList.isEmpty()){
            return true;
        } else return false;
    }

    @Override
    public void delete(EvaluatePosts evaluatePosts) {
        em.remove(evaluatePosts);
    }

    @Override
    public List<EvaluatePosts> findAllByUserId(Long userId) {

        List resultList = em.createQuery("SELECT p from EvaluatePosts p join p.user u WHERE u.id = :id ORDER BY p.modifiedDate DESC")
                .setParameter("id", userId)
                .getResultList();

        return resultList;
    }
}
