package usw.suwiki.domain.lecture.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.evaluatepost.fixture.EvaluatePostFixture;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.LectureDetail;
import usw.suwiki.domain.lecture.domain.LectureSchedule;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.fixture.LectureFixture;
import usw.suwiki.domain.user.fixture.UserFixture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.annotation.SuwikiJpaTest;

@SuwikiJpaTest
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
        this.dummyUser = userRepository.save(UserFixture.createDummyUser());
        this.dummyLecture = lectureRepository.save(LectureFixture.createFirstDummyLecture());
        this.dummyEvaluatePost = EvaluatePostFixture.createFirstDummyEvaluatePost(dummyUser, dummyLecture);

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
            Lecture dummyLecture1 = LectureFixture.createDummyLecture(
                    "2021-2, 2022-2, " + currentSemester,
                    "도전과 창조",
                    "중핵",
                    "교양 아님",
                    "우문균",
                    firstGradeLectureDetail);
            LectureSchedule.builder()
                    .placeSchedule("IT123(월1,2,3)")
                    .lecture(dummyLecture1)
                    .build();
            lectureRepository.save(dummyLecture1);

            Lecture dummyLecture2 = LectureFixture.createDummyLecture(
                    "2021-2, 2022-2, " + currentSemester,
                    "테스트학개론",
                    "중핵",
                    "교양 아님",
                    "장성태",
                    firstGradeLectureDetail
            );
            LectureSchedule.builder()
                    .placeSchedule("IT123(월1,2,3)")
                    .lecture(dummyLecture2)
                    .build();
            lectureRepository.save(dummyLecture2);

            Lecture dummyLecture3 = LectureFixture.createDummyLecture(
                    "2021-2, 2022-2, " + currentSemester,
                    "도전과 창조",
                    "중핵",
                    "교양",
                    "소크라테스",
                    firstGradeLectureDetail
            );
            LectureSchedule.builder()
                    .placeSchedule("IT123(월1,2,3)")
                    .lecture(dummyLecture3)
                    .build();
            lectureRepository.save(dummyLecture3);

            Lecture dummyLecture4 = LectureFixture.createDummyLecture(
                    "2021-2, 2022-2, " + currentSemester,
                    "도전과 창조",
                    "중핵",
                    "교양",
                    "우문균",
                    secondGradeLectureDetail
            );
            LectureSchedule.builder()
                    .placeSchedule("IT123(월1,2,3)")
                    .lecture(dummyLecture4)
                    .build();
            lectureRepository.save(dummyLecture4);
        }

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
        String lectureNameKeyword = "도전";
        String professorNameKeyword = "장성태";
        String bothKeyword = "테스";
        String majorType = "교양";
        int grade = 2;

        // when
        Slice<Lecture> currentSemesterResult = lectureRepository.findCurrentSemesterLectures(
                cursorId,
                limit,
                null,
                null,
                null
        );
        Slice<Lecture> lectureKeywordResult = lectureRepository.findCurrentSemesterLectures(
                cursorId,
                limit,
                lectureNameKeyword,
                null,
                null
        );
        Slice<Lecture> professorKeywordResult = lectureRepository.findCurrentSemesterLectures(
                cursorId,
                limit,
                professorNameKeyword,
                null,
                null
        );
        Slice<Lecture> bothKeywordResult = lectureRepository.findCurrentSemesterLectures(
                cursorId,
                limit,
                bothKeyword,
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
        assertThat(currentSemesterResult.getContent().size()).isEqualTo(80);
        assertThat(lectureKeywordResult.getContent().size()).isEqualTo(60);
        assertThat(professorKeywordResult.getContent().size()).isEqualTo(20);
        assertThat(bothKeywordResult.getContent().size()).isEqualTo(40);    // 강의명 테스트학개론, 교수명 소크라테스
        assertThat(majorResult.getContent().size()).isEqualTo(40);
        assertThat(majorGradeResult.getContent().size()).isEqualTo(20);
    }
}
