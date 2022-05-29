package usw.suwiki.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.User;

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

    List<User> findByRequestedQuitDate(LocalDateTime localDateTime);

    //loginId, email 입력값 검증
    @Query(value = "SELECT loginId, email FROM User WHERE loginId = :loginId and email = :email")
    String findPwLogicByLoginIdAndEmail(@Param("loginId") String loginId, @Param("email") String email);

    //loginId와 email 에 일치하는 유저의 비밀번호 변경
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User Set password = :resetPassword WHERE loginId = :loginId and email = :email")
    void resetPassword(@Param("resetPassword") String resetPassword, @Param("loginId") String loginId, @Param("email") String email);

    //User 비밀번호 수정 (마이페이지에서 비밀번호 재 설정)
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User Set password = :editMyPassword WHERE loginId = :loginId")
    void editPassword(@Param("editMyPassword") String editMyPassword, @Param("loginId") String loginId);

    //User 삭제
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE from User WHERE id = :id")
    void deleteUserNotEmailCheck(@Param("id") Long id);

    //격리테이블에 본 테이블 데이터 옮기기
    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO user SELECT id, login_id, password, email, restricted, role, written_evaluation, written_exam, view_exam_count, point, last_login, requested_quit_date, created_at, updated_at FROM user_isolation WHERE id = :id", nativeQuery = true)
    void insertUserIsolationIntoUser(@Param("id") Long id);

    
    //UserIdx 로 블랙리스트 출소 유저 반영해주기
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User SET restricted = false WHERE id = :userIdx")
    void unRestricted(@Param("userIdx") Long userIdx);
}
