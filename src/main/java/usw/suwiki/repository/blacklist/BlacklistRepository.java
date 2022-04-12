package usw.suwiki.repository.blacklist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistDomain.BlacklistDomain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistDomain, Long> {

    //블랙리스트 기간 종료 유저 리스트화
    List<BlacklistDomain> findByExpiredAtBefore(LocalDateTime targetTime);

    ///유저 인덱스로 객체 불러오기
    Optional<BlacklistDomain> findByUserId(Long id);

    Optional<BlacklistDomain> findByHashedEmail(String email);

    //블랙리스트 유저 삭제
    void deleteByUserId(Long id);

//    void save(String email, Long id);

    @Modifying
    @Query(value = "INSERT INTO blacklist_domain VALUES :email WHERE user_idx = :id", nativeQuery = true)
    void insertIntoHashEmailAndExpiredAt(@Param("email") String email, @Param("id") Long id);


}
