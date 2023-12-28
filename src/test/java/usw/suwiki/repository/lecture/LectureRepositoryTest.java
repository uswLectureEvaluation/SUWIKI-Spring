package usw.suwiki.repository.lecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import usw.suwiki.config.TestJpaConfig;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.template.evaluatepost.EvaluatePostTemplate;
import usw.suwiki.template.lecture.LectureTemplate;
import usw.suwiki.template.user.UserTemplate;

@DataJpaTest
@Import(TestJpaConfig.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class LectureRepositoryTest {    // TODO: https://7357.tistory.com/339 보면서 동시성 테스트하는 방법 공부
    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User dummyUser;
    private Lecture dummyLecture;
    private EvaluatePost dummyEvaluatePost;

    @BeforeEach
    void setUp() {
        this.dummyUser = userRepository.save(UserTemplate.createDummyUser());
        this.dummyLecture = lectureRepository.save(LectureTemplate.createFirstDummyLecture());
        this.dummyEvaluatePost = EvaluatePostTemplate.createFirstDummyEvaluatePost(dummyUser, dummyLecture);

        entityManager.clear();
    }

    @Test
    @DisplayName("Lecture 단일 조회 성공")
    public void selectLecture_success() {
        // given
        Long id = dummyLecture.getId();

        // when
        Optional<Lecture> foundLecture = lectureRepository.findById(id);

        // then
        assertThat(foundLecture.isPresent()).isTrue();
        assertThat(foundLecture.get().getName())
                .isEqualTo(dummyLecture.getName());
        assertThat(foundLecture.get().getLectureDetail().getCode())
                .isEqualTo(dummyLecture.getLectureDetail().getCode());
    }

    @Test
    @DisplayName("Lecture 단일 조회 성공 - 비관적 락 조회")
    public void selectLecture_success_with_pessimistic_lock() {
        // given
        Long id = dummyLecture.getId();

        // when
        Optional<Lecture> foundLecture = lectureRepository.findForUpdateById(id);

        // then
        assertThat(foundLecture.isPresent()).isTrue();
        assertThat(foundLecture.get().getName())
                .isEqualTo(dummyLecture.getName());
        assertThat(foundLecture.get().getLectureDetail().getCode())
                .isEqualTo(dummyLecture.getLectureDetail().getCode());
    }
}
