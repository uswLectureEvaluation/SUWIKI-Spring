package usw.suwiki.domain.favorite_major;

import org.springframework.stereotype.Repository;

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
    public List<FavoriteMajor> findAllByUser(Long userIdx) {
        List resultList = em.createQuery("SELECT f from FavoriteMajor f join f.user u WHERE u.id = :id")
                .setParameter("id", userIdx)
                .getResultList();

        return resultList;
    }

    @Override
    public List<String> findOnlyMajorTypeByUser(Long userIdx) {
        List resultList = em.createQuery("SELECT f.majorType from FavoriteMajor f join f.user u WHERE u.id = :id")
                .setParameter("id", userIdx)
                .getResultList();

        return resultList;
    }

    @Override
    public FavoriteMajor findByUserAndMajorType(Long userIdx, String majorType) {
        List resultList = em.createQuery("SELECT f from FavoriteMajor f join f.user u WHERE u.id = :id AND f.majorType = :majorType")
                .setParameter("id", userIdx)
                .setParameter("majorType", majorType)
                .getResultList();

        FavoriteMajor result = (FavoriteMajor) resultList.get(0);

        return result;
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
