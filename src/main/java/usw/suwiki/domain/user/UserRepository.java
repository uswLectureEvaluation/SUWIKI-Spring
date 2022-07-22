package usw.suwiki.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userIdx);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    List<User> findByLastLoginBefore(LocalDateTime localDateTime);

    List<User> findByRequestedQuitDateBefore(LocalDateTime localDateTime);

    // UserIdx 로 정지 해제
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User SET restricted = false WHERE id = :userIdx")
    void unRestricted(@Param("userIdx") Long userIdx);

    //loginId, email 입력값 검증
    @Query(value = "SELECT loginId, email FROM User WHERE loginId = :loginId and email = :email")
    String findPwLogicByLoginIdAndEmail(@Param("loginId") String loginId, @Param("email") String email);

    //loginId와 email 에 일치하는 유저의 비밀번호 변경
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User Set password = :resetPassword WHERE loginId = :loginId and email = :email")
    void resetPassword(@Param("resetPassword") String resetPassword, @Param("loginId") String loginId, @Param("email") String email);

    /**

     휴면계정 테이블의 userIdx, loginId, password, Email 불러오기

     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE user SET " +
            "id = (SELECT user_idx FROM user_isolation WHERE user_idx = :id)," +
            "login_id = (SELECT login_id FROM user_isolation WHERE user_idx = :id)," +
            "password = (SELECT password FROM user_isolation WHERE user_idx = :id)," +
            "email = (SELECT email FROM user_isolation WHERE user_idx = :id)" +
            "last_login = (SELECT last_login FROM user_isolation WHERE user_idx = :id)" +
            "requested_quit_date = (SELECT requested_date FROM user_isolation WHERE user_idx = :id)", nativeQuery = true)
    void convertToWakeUp(@Param("id") Long id);


    //User 비밀번호 수정 (마이페이지에서 비밀번호 재 설정)
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User Set password = :editMyPassword WHERE loginId = :loginId")
    void editPassword(@Param("editMyPassword") String editMyPassword, @Param("loginId") String loginId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE user SET login_id = null, password = null, email = null WHERE id = :id", nativeQuery = true)
    void convertToSleeping(@Param("id") Long id);

}
