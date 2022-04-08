package usw.suwiki.repository.notice;
import usw.suwiki.domain.notice.Notice;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class JpaNoticeRepository implements NoticeRepository{

    private final EntityManager em;

    public JpaNoticeRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Notice notice) {
        em.persist(notice);
    }

    @Override
    public List<Notice> findByNoticeList() {

        List resultList = em.createQuery("SELECT n from Notice n ORDER BY n.modifiedDate")
                .getResultList();

        return resultList;
    }

    @Override
    public void delete(Notice notice) {
        em.remove(notice);
    }

    @Override
    public Notice findById(Long id) {
        return em.find(Notice.class,id);
    }

}

