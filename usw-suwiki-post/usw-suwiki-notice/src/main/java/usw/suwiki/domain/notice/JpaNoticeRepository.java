package usw.suwiki.domain.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import usw.suwiki.global.PageOption;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaNoticeRepository implements NoticeRepository {
    private final EntityManager em;

    @Override
    public void save(Notice notice) {
        em.persist(notice);
    }

    @Override
    public List findByNoticeList(PageOption page) {
        Optional<Integer> pageNumber = page.getPageNumber();
        return em.createQuery("SELECT n from Notice n ORDER BY n.modifiedDate DESC")
            .setFirstResult((pageNumber.get()) * 10)
            .setMaxResults(10)
            .getResultList();
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

