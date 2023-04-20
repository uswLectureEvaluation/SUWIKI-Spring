package usw.suwiki.domain.user.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.user.restrictinguser.repository.RestrictingUser;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RestrictingUserRepository extends JpaRepository<RestrictingUser, Long> {

    @Query(value = "SELECT * FROM restricting_user WHERE user_idx = :userIdx", nativeQuery = true)
    List<RestrictingUser> findByUserIdx(@Param("userIdx") Long userIdx);

    List<RestrictingUser> findByRestrictingDateBefore(LocalDateTime localDateTime);

    @Modifying
    @Query(value = "DELETE FROM restricting_user WHERE user_idx = :userIdx", nativeQuery = true)
    void deleteByUserIdx(@Param("userIdx") Long userIdx);
}
