package usw.suwiki.domain.user.major;

import java.util.List;

public interface FavoriteMajorRepository {

    FavoriteMajor findById(Long id);

    List<FavoriteMajor> findAllByUser(Long userIdx);

    void save(FavoriteMajor favoriteMajor);

    void delete(FavoriteMajor favoriteMajor);

    FavoriteMajor findByUserAndMajorType(Long userIdx, String majorType);

    List<String> findOnlyMajorTypeByUser(Long userIdx);

}
