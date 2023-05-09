package usw.suwiki.domain.exam.domain.repository;

import org.springframework.stereotype.Repository;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.PageOption;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaExamPostsRepository implements ExamPostsRepository {

    private final EntityManager em;

    public JpaExamPostsRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(ExamPosts examPosts) {
        em.persist(examPosts);
    }

    @Override
    public List<ExamPosts> findByLectureId(PageOption option, Long lectureId) {
        Optional<Integer> page = option.getPageNumber();
        if (page.isEmpty()) {
            page = Optional.of(1);
        }

        List resultList = em.createQuery("SELECT p from ExamPosts p join p.lecture l WHERE l.id = :lectureId ORDER BY p.modifiedDate DESC")
                .setParameter("lectureId", lectureId)
                .setFirstResult((page.get() - 1) * 10)
                .setMaxResults(10)
                .getResultList();

        return resultList;
    }

    @Override
    public ExamPosts findById(Long id) {
        return em.find(ExamPosts.class, id);
    }

    @Override
    public List<ExamPosts> findByUserId(PageOption option, Long userId) {

        Optional<Integer> page = option.getPageNumber();
        if (page.isEmpty()) {
            page = Optional.of(1);
        }

        List resultList = em.createQuery("SELECT p from ExamPosts p join p.user u WHERE u.id = :id ORDER BY p.modifiedDate DESC")
                .setParameter("id", userId)
                .setFirstResult((page.get() - 1) * 10)
                .setMaxResults(10)
                .getResultList();

        return resultList;
    }

    @Override
    public boolean isWrite(User user, Lecture lecture) {
        List resultList = em.createQuery("SELECT p from ExamPosts p WHERE p.user = :user AND p.lecture = :lecture")
                .setParameter("user", user)
                .setParameter("lecture", lecture)
                .getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else return true;
    }

    @Override
    public void delete(ExamPosts examPosts) {
        em.remove(examPosts);
    }

    @Override
    public List<ExamPosts> findAllByUserId(Long userId) {

        List resultList = em.createQuery("SELECT p from ExamPosts p join p.user u WHERE u.id = :id ORDER BY p.modifiedDate DESC")
                .setParameter("id", userId)
                .getResultList();

        return resultList;
    }
}
