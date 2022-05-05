package usw.suwiki.repository.favorite_major;

import org.springframework.stereotype.Repository;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.favorite_major.FavoriteMajor;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class JpaFavoriteMajorRepository implements FavoriteMajorRepository{

    private final EntityManager em;

    public JpaFavoriteMajorRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public FavoriteMajor findById(Long id) {
        FavoriteMajor favorite = em.find(FavoriteMajor.class, id);
        return favorite;
    }

    @Override
    public List<FavoriteMajor> findByUser(Long userIdx) {
        List resultList = em.createQuery("SELECT p from FavoriteMajor f join f.user u WHERE u.id = :id")
                .setParameter("id", userIdx)
                .getResultList();

        return resultList;
    }

    @Override
    public void save(FavoriteMajor favoriteMajor) {
        em.persist(favoriteMajor);
    }

    @Override
    public void delete(FavoriteMajor favoriteMajor) {
        em.remove(favoriteMajor);
    }

}
