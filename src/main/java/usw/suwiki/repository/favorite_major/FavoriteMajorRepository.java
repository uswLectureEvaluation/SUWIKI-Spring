package usw.suwiki.repository.favorite_major;

import usw.suwiki.domain.favorite_major.FavoriteMajor;

import java.util.List;

public interface FavoriteMajorRepository {
    FavoriteMajor findById(Long id);

    List<FavoriteMajor> findByUser(Long userIdx);

    void save(FavoriteMajor favoriteMajor);

    void delete(FavoriteMajor favoriteMajor);

}
