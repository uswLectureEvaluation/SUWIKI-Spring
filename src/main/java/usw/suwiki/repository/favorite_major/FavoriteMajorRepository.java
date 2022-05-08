package usw.suwiki.repository.favorite_major;

import usw.suwiki.domain.favorite_major.FavoriteMajor;

import java.util.List;

public interface FavoriteMajorRepository {
    FavoriteMajor findById(Long id);

    List<FavoriteMajor> findAllByUser(Long userIdx);

    void save(FavoriteMajor favoriteMajor);

    void delete(FavoriteMajor favoriteMajor);

    FavoriteMajor findByUserAndMajorType(Long userIdx, String majorType);

    List<String> findOnlyMajorTypeByUser(Long userIdx);

}
