package usw.suwiki.domain.blacklistDomain;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistDomain, Long> {

    // 블랙리스트 기간 종료 유저 리스트화
    List<BlacklistDomain> findByExpiredAtBefore(LocalDateTime targetTime);

    // 유저 인덱스로 객체 불러오기 Optional 타입
    @Query(value = "SELECT * FROM blacklist_domain WHERE user_idx = :userIdx", nativeQuery = true)
    Optional<BlacklistDomain> findByUserId(@Param("userIdx") Long userIdx);

    // 유저 인덱스로 객체 불러오기 List 타입
    @Query(value = "SELECT * FROM blacklist_domain WHERE user_idx = :userIdx", nativeQuery = true)
    List<BlacklistDomain> findByUserIdx(@Param("userIdx") Long userIdx);

    // 이메일 해싱으로 블랙리스트 인지 구분
    Optional<BlacklistDomain> findByHashedEmail(String email);

    //블랙리스트 유저 삭제
    void deleteByUserIdx(Long id);
}