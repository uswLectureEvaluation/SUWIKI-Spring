package usw.suwiki.domain.favoritemajor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.favoritemajor.FavoriteMajor;

import java.util.List;
import java.util.Optional;

public interface FavoriteMajorRepository extends JpaRepository<FavoriteMajor, Long> {

    List<FavoriteMajor> findAllByUserId(Long userId);   //findAllUser, findOnlyMajorTypeByUser - 이거 활용
    boolean existsByUserIdAndMajorType(Long userId, String majorType);
    Optional<FavoriteMajor> findByUserIdAndMajorType(Long userId, String majorType);

}
