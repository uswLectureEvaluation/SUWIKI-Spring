package usw.suwiki.domain.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.testconfig.TestConfig;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
class CustomUserRepositoryImplTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @DisplayName("유저 정지 여부 업데이트 DB 적용 테스트")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void updateRestricted(Boolean flag) {
        User user = userRepository.findById(6L).get();
        userRepository.updateRestricted(user.getId(), flag);
        entityManager.clear();
        assertThat(userRepository.findById(6L).get().getRestricted())
                .isEqualTo(flag);
    }

    @DisplayName("유저 정지횟수 업데이트 DB 적용 테스트")
    @ParameterizedTest
    @ValueSource(ints = {999, 0, -123, 1, 2, 4, -1})
    void updateRestrictedCount(Integer restrictedCount) {
        User user = userRepository.findById(6L).get();
        int preRestrictedCount = user.getRestrictedCount();
        userRepository.updateRestrictedCount(user.getId(), preRestrictedCount + restrictedCount);
        entityManager.clear();
        assertThat(userRepository.findById(6L).get().getRestrictedCount())
                .isEqualTo(preRestrictedCount + restrictedCount);
    }

    @DisplayName("유저 포인트 업데이트 DB 적용 테스트")
    @ParameterizedTest
    @ValueSource(ints = {999, 0, -123, 1, 2, 4, -1})
    void updatePoint(Integer point) {
        User user = userRepository.findById(6L).get();
        int prePoint = user.getPoint();
        userRepository.updatePoint(user.getId(), (user.getPoint() - point));
        entityManager.clear();
        assertThat(userRepository.findById(6L).get().getPoint())
                .isEqualTo(prePoint - point);
    }

    @DisplayName("유저 시험정보 작성 횟수 업데이트 DB 적용 테스트")
    @ParameterizedTest
    @ValueSource(ints = {999, 0, -123, 1, 2, 4, -1})
    void updateWrittenExamCount(Integer writtenExamCount) {
        User user = userRepository.findById(6L).get();
        int prePoint = user.getPoint();
        userRepository.updatePoint(user.getId(), (user.getPoint() - writtenExamCount));
        entityManager.clear();
        assertThat(userRepository.findById(6L).get().getPoint())
                .isEqualTo(prePoint - writtenExamCount);
    }

    @DisplayName("유저 강의평가 작성 횟수 업데이트 DB 적용 테스트")
    @ParameterizedTest
    @ValueSource(ints = {999, 0, -123, 1, 2, 4, -1})
    void updateWrittenEvaluateCount(Integer writtenEvaluateCount) {
        User user = userRepository.findById(6L).get();
        int prePoint = user.getPoint();
        userRepository.updateWrittenEvaluateCount(user.getId(), (user.getPoint() - writtenEvaluateCount));
        entityManager.clear();
        assertThat(userRepository.findById(6L).get().getPoint())
                .isEqualTo(prePoint - writtenEvaluateCount);
    }

    @DisplayName("유저 시험정보 조회 횟수 업데이트 DB 적용 테스트")
    @ParameterizedTest
    @ValueSource(ints = {999, 0, -123, 1, 2, 4, -1})
    void updateViewExamCount(Integer viewExamCount) {
        User user = userRepository.findById(6L).get();
        int preViewExamCount = user.getViewExamCount();
        userRepository.updateViewExamCount(user.getId(), (user.getViewExamCount() - viewExamCount));
        entityManager.clear();
        assertThat(userRepository.findById(6L).get().getPoint())
                .isEqualTo(preViewExamCount - viewExamCount);
    }

    @DisplayName("유저 이메일 인증 테스트 (이메일 인증 시 UpdatedAt, CreatedAt 컬럼이 null에서 현재 시간으로 값이 변한다.)")
    @Test
    void updateUserEmailAuthStatus() {
        User user = new User();
        userRepository.save(user);
        entityManager.flush();
        userRepository.updateUserEmailAuthStatus(user.getId());
        entityManager.clear();
        assertThat(userRepository.findById(6L).get().getUpdatedAt())
                .isNotNull();
    }

    @Test
    void updatePassword() {
    }

    @Test
    void applyUserSoftDelete() {
    }

    @Test
    void unapplyUserSoftDelete() {
    }
}