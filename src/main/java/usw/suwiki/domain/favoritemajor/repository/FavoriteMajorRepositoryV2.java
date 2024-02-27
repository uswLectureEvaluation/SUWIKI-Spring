package usw.suwiki.domain.favoritemajor.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.favoritemajor.FavoriteMajor;

public interface FavoriteMajorRepositoryV2 extends JpaRepository<FavoriteMajor, Long> {

    List<FavoriteMajor> findAllByUserId(Long userId);

    boolean existsByUserIdAndMajorType(Long userId, String majorType);

    Optional<FavoriteMajor> findByUserIdAndMajorType(Long userId, String majorType);

}
