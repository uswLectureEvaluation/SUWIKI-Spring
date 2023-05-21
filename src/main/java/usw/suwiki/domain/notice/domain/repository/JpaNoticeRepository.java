package usw.suwiki.domain.notice.domain.repository;

import org.springframework.stereotype.Repository;
import usw.suwiki.domain.notice.domain.Notice;
import usw.suwiki.global.PageOption;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaNoticeRepository implements NoticeRepository {

    private final EntityManager em;

    public JpaNoticeRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Notice notice) {
        em.persist(notice);
    }

    @Override
    public List<Notice> findByNoticeList(PageOption page) {
        Optional<Integer> pageNumber = page.getPageNumber();
        List resultList = em.createQuery("SELECT n from Notice n ORDER BY n.modifiedDate DESC")
                .setFirstResult((pageNumber.get() - 1) * 10)
                .setMaxResults(10)
                .getResultList();
        return resultList;
    }

    @Override
    public void delete(Notice notice) {
        em.remove(notice);
    }

    @Override
    public Notice findById(Long id) {
        return em.find(Notice.class, id);
    }

}

