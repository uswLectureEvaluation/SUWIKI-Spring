package usw.suwiki.repository.lecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import usw.suwiki.config.TestJpaConfig;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.LectureDetail;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.template.evaluatepost.EvaluatePostTemplate;
import usw.suwiki.template.lecture.LectureTemplate;
import usw.suwiki.template.user.UserTemplate;

// TODO: RepositoryTest 슈퍼 클래스로 공통 설정 상속
// TODO refactor: 테스트 독립성 보장. TRUNCATE 실행
@DataJpaTest
@Import(TestJpaConfig.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
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

    @Value("${business.current-semester}")
    private String currentSemester;

    @BeforeEach
    void setUp() {
        this.dummyUser = userRepository.save(UserTemplate.createDummyUser());
        this.dummyLecture = lectureRepository.save(LectureTemplate.createFirstDummyLecture());
        this.dummyEvaluatePost = EvaluatePostTemplate.createFirstDummyEvaluatePost(dummyUser, dummyLecture);

        LectureDetail firstGradeLectureDetail = LectureDetail.builder()
                .grade(1)
                .evaluateType("상대평가")
                .point(2.0)
                .build();
        LectureDetail secondGradeLectureDetail = LectureDetail.builder()
                .grade(2)
                .evaluateType("상대평가")
                .point(2.0)
                .build();

        /*  테스트 시나리오 (강의 리스트 조회 - 시간표상 이번 학기에 열린 강의 리스트 조회)

            총 과목 개수는 21개
            semester: 이번 학기인 과목은 80개
            name: 이번 학기중 "도전과 창조"가 포함된 과목은 60개
            major: 이번 학기중 "교양"인 과목은 40개, "교양(야)"인 과목은 10개
            grade: 이번 학기중 2학년 과목은 20개
         */
        for (int i = 0; i < 20; i++) {
            lectureRepository.save(LectureTemplate.createDummyLecture(
                    "2021-2, 2022-2, " + currentSemester,
                    "도전과 창조",
                    "중핵",
                    "교양 아님",
                    "우문균",
                    firstGradeLectureDetail
            ));
            lectureRepository.save(LectureTemplate.createDummyLecture(
                    "2021-2, 2022-2, " + currentSemester,
                    "테스트학개론",
                    "중핵",
                    "교양 아님",
                    "우문균",
                    firstGradeLectureDetail
            ));
            lectureRepository.save(LectureTemplate.createDummyLecture(
                    "2021-2, 2022-2, " + currentSemester,
                    "도전과 창조",
                    "중핵",
                    "교양",
                    "우문균",
                    firstGradeLectureDetail
            ));
            lectureRepository.save(LectureTemplate.createDummyLecture(
                    "2021-2, 2022-2, " + currentSemester,
                    "도전과 창조",
                    "중핵",
                    "교양",
                    "우문균",
                    secondGradeLectureDetail
            ));
        }

        Slice<Lecture> result = lectureRepository.findCurrentSemesterLectures(
                0L,
                20,
                null, null, null
        );
        System.out.println("result = " + result);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        lectureRepository.deleteAll();

        entityManager.clear();
    }

    @Test
    @DisplayName("강의 단일 조회")
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
    @DisplayName("강의 단일 조회 - 비관적 락")
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

    @Test
    @DisplayName("시간표 강의 리스트 조회 - 이번 학기에 열린 강의 리스트 조회")
    public void selectTimetableLectureList_success() {
        // given
        long cursorId = 0;
        int limit = 80;
        String keyword = "도전";
        String majorType = "교양";
        int grade = 2;
        System.out.println("lectureRepository.count() = " + lectureRepository.count());

        // TODO fix: Slice 0 containing UNKNOWN instances
        // when
        Slice<Lecture> currentSemester = lectureRepository.findCurrentSemesterLectures(
                cursorId,
                limit,
                null,
                null,
                null
        );
        Slice<Lecture> keywordResult = lectureRepository.findCurrentSemesterLectures(
                cursorId,
                limit,
                keyword,
                null,
                null
        );
        Slice<Lecture> majorResult = lectureRepository.findCurrentSemesterLectures(
                cursorId,
                limit,
                null,
                majorType,
                null
        );
        Slice<Lecture> majorGradeResult = lectureRepository.findCurrentSemesterLectures(
                cursorId,
                limit,
                null,
                majorType,
                grade
        );

        // then
        assertThat(currentSemester.getContent().size()).isEqualTo(80);
        assertThat(keywordResult.getContent().size()).isEqualTo(60);
        assertThat(majorResult.getContent().size()).isEqualTo(40);
        assertThat(majorGradeResult.getContent().size()).isEqualTo(20);
    }
}
