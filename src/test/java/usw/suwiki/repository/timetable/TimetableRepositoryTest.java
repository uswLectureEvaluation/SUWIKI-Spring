package usw.suwiki.repository.timetable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import usw.suwiki.config.TestJpaConfig;
import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.repository.TimetableRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.template.timetable.TimetableTemplate;
import usw.suwiki.template.user.UserTemplate;

@DataJpaTest
@Import(TestJpaConfig.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TimetableRepositoryTest {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User dummyUser;
    private Timetable dummyTimetable;

    @BeforeEach
    void setUp() {
        this.dummyUser = userRepository.save(UserTemplate.createDummyUser());

        Timetable timetable = TimetableTemplate.createFirstDummyTimetable(dummyUser);
        this.dummyTimetable = timetableRepository.save(timetable);

        TimetableTemplate.createCustomDummyTimetable("1-1 시간표", 2017, Semester.FIRST, dummyUser);
        TimetableTemplate.createCustomDummyTimetable("1-2 시간표", 2017, Semester.SECOND, dummyUser);
        TimetableTemplate.createCustomDummyTimetable("2-1 시간표", 2018, Semester.FIRST, dummyUser);

        userRepository.save(dummyUser);
    }

    @Test
    @DisplayName("INSERT Timetable 성공 - User 연관관계 편의 메서드")
    public void insertTimetable_success_user_association_method() { // TODO: remove 연관관계 메서드 테스트
        // given
        Timetable validTimetable = Timetable.builder()
                .name("첫 학기")
                .year(2017)
                .semester(Semester.FIRST)
                .build();

        // when
        validTimetable.associateUser(dummyUser);    // 연관관계 편의 메서드
        entityManager.persist(dummyUser);   // 유저 영속화
        entityManager.flush();
        entityManager.clear();

        // then
        User foundUser = entityManager.find(User.class, dummyUser.getId());
        Timetable foundTable = entityManager.find(Timetable.class, validTimetable.getId());

        assertThat(foundTable.getName()).isEqualTo(validTimetable.getName());
        assertThat(foundTable.getUser()).isEqualTo(foundUser);
    }

    @Test
    @DisplayName("INSERT Timetable 실패 - NOT NULL 제약조건 위반")
    public void insertTimetable_fail_notnull_constraint() {
        // given
        Timetable nullNameTimetable = Timetable.builder()
                .name(null)
                .year(2017)
                .semester(Semester.FIRST)
                .build();
        nullNameTimetable.associateUser(dummyUser);

        Timetable nullYearTimetable = Timetable.builder()
                .name("첫 학기")
                .year(null)
                .semester(Semester.FIRST)
                .build();
        nullYearTimetable.associateUser(dummyUser);

        Timetable nullSemesterTimetable = Timetable.builder()
                .name("첫 학기")
                .year(2017)
                .semester(null)
                .build();
        nullSemesterTimetable.associateUser(dummyUser);

        // when & then
        assertThatThrownBy(() -> timetableRepository.save(nullNameTimetable))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableRepository.save(nullYearTimetable))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableRepository.save(nullSemesterTimetable))
                .isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("SELECT Timetable id 조회 성공")
    public void selectTimetableById_success() {
        // given
        Long id = dummyTimetable.getId();

        // when
        Optional<Timetable> optionalTimetable = timetableRepository.findById(id);

        // then
        assertThat(optionalTimetable.isPresent()).isTrue();
        assertThat(optionalTimetable.get()).isEqualTo(dummyTimetable);
    }

    @Test
    @DisplayName("SELECT ALL Timetable userId 조회 성공")
    public void selectAllTimetableByUserId_success() {
        // given
        Long userId = dummyUser.getId();

        // when
        List<Timetable> all = timetableRepository.findAllByUserId(userId);

        // then
        assertThat(all.isEmpty()).isFalse();
        assertThat(all.size()).isEqualTo(4);
    }

    // TODO: 연관관계 메서드를 이용한 삭제 구현 고민

    // TimetableCell

    @Test
    @DisplayName("SELECT ALL TimetableCell by TimetableRepository 성공")
    public void selectAllTimetableCellByTimetable_success() {
        // when
        Optional<Timetable> timetable = timetableRepository.findById(dummyTimetable.getId());   // TODO: QueryDSL 버전

        // then
        assertThat(timetable.isPresent()).isTrue();
        List<TimetableCell> cellList = timetable.get().getCellList();
        assertThat(cellList.size()).isEqualTo(3);
        assertThat(cellList.get(0).getLectureName()).isEqualTo("데이터 구조");
        assertThat(cellList.get(1).getLectureName()).isEqualTo("컴퓨터 구조");
        assertThat(cellList.get(2).getLectureName()).isEqualTo("이산 구조");
    }
}
