package usw.suwiki.repository.timetable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static usw.suwiki.domain.timetable.entity.TimetableCellColor.BROWN;
import static usw.suwiki.domain.timetable.entity.TimetableCellColor.GRAY;
import static usw.suwiki.domain.timetable.entity.TimetableCellColor.ORANGE;

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
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.repository.TimetableCellRepository;
import usw.suwiki.domain.timetable.repository.TimetableRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.template.timetable.TimetableTemplate;
import usw.suwiki.template.timetablecell.TimetableCellTemplate;
import usw.suwiki.template.user.UserTemplate;

@DataJpaTest
@Import(TestJpaConfig.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TimetableRepositoryTest {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private TimetableCellRepository timetableCellRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User dummyUser;
    private Timetable dummyTimetable;
    private TimetableCell dummyTimetableCell;

    @BeforeEach
    void setUp() {
        this.dummyUser = userRepository.save(UserTemplate.createDummyUser());

        Timetable timetable = TimetableTemplate.createFirstDummy(dummyUser);
        this.dummyTimetable = timetableRepository.save(timetable);

        TimetableTemplate.createDummy("1-1 시간표", 2017, Semester.FIRST, dummyUser);
        TimetableTemplate.createDummy("1-2 시간표", 2017, Semester.SECOND, dummyUser);
        TimetableTemplate.createDummy("2-1 시간표", 2018, Semester.FIRST, dummyUser);
        userRepository.save(dummyUser);

        TimetableCell timetableCell = TimetableCellTemplate.createFirstDummy(dummyTimetable);
        this.dummyTimetableCell = timetableCellRepository.save(timetableCell);
        TimetableCellTemplate.createDummy("데이터 구조", "손수국", GRAY, dummyTimetable);
        TimetableCellTemplate.createDummy("컴퓨터 구조", "갓성태", ORANGE, dummyTimetable);
        TimetableCellTemplate.createDummy("이산 구조", "김장영", BROWN, dummyTimetable);
        timetableRepository.save(dummyTimetable);
    }

    /**
     * Timetable
     */
    @Test
    @DisplayName("Timetable 삽입 성공 - 연관관계 확인")
    public void insertTimetable_success_user_association_method() {
        // given
        Timetable timetable = Timetable.builder()
                .name("첫 학기")
                .year(2017)
                .semester(Semester.FIRST)
                .build();
        timetable.associateUser(dummyUser);

        // when
        timetableRepository.save(timetable);
        entityManager.clear();

        User foundUser = entityManager.find(User.class, dummyUser.getId());
        Optional<Timetable> foundTable = timetableRepository.findById(timetable.getId());

        // then
        assertThat(foundTable.isPresent()).isTrue();
        assertThat(foundTable.get().getName()).isEqualTo(timetable.getName());
        assertThat(foundUser.getTimetableList()).contains(foundTable.get());
    }

    @Test
    @DisplayName("Timetable 삽입 실패 - NOT NULL 제약조건 위반")
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
    @DisplayName("Timetable 단일 조회 성공 - findById")
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
    @DisplayName("Timetable 리스트 조회 성공")
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

    /**
     * TimetableCell
     */
    @Test
    @DisplayName("TimetableCell 삽입 성공 - 연관관계 확인")
    public void insertTimetableCell_success() {
        // given
        TimetableCell timetableCell = TimetableCell.builder()
                .lectureName("")        // blank 가능
                .professorName("")      // blank 가능
                .color(TimetableCellColor.BROWN)
                .build();
        timetableCell.associateTimetable(dummyTimetable);   // 연관관계 편의 메서드

        // when
        timetableCellRepository.save(timetableCell);
        entityManager.clear();

        Timetable foundTable = entityManager.find(Timetable.class, dummyTimetable.getId());
        Optional<TimetableCell> optionalCell = timetableCellRepository.findById(timetableCell.getId());

        // then
        assertThat(optionalCell.isPresent()).isTrue();
        assertThat(optionalCell.get().getLectureName()).isEqualTo(timetableCell.getLectureName());
        assertThat(foundTable.getCellList()).contains(optionalCell.get());
    }

    @Test
    @DisplayName("TimetableCell 삽입 실패 - NOT NULL 제약조건 위반")
    public void insertTimetableCell_fail_notnull_constraint() {
        // given
        TimetableCell nullLectureNameCell = TimetableCell.builder()
                .lectureName(null)
                .professorName("신호진")
                .color(TimetableCellColor.BROWN)
                .build();
        nullLectureNameCell.associateTimetable(dummyTimetable);

        TimetableCell nullProfessorNameCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName(null)
                .color(TimetableCellColor.BROWN)
                .build();
        nullProfessorNameCell.associateTimetable(dummyTimetable);

        TimetableCell nullColorCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName("신호진")
                .color(null)
                .build();
        nullColorCell.associateTimetable(dummyTimetable);

        // when & then
        assertThatThrownBy(() -> timetableCellRepository.save(nullLectureNameCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(nullProfessorNameCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(nullColorCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("TimetableCell 단일 조회 성공")
    public void selectTimetableCell_success() {
        // given
        Long id = dummyTimetableCell.getId();

        // when
        Optional<TimetableCell> optionalTimetableCell = timetableCellRepository.findById(id);

        // then
        assertThat(optionalTimetableCell.isPresent()).isTrue();
        assertThat(optionalTimetableCell.get()).isEqualTo(dummyTimetableCell);
    }

    @Test
    @DisplayName("TimetableCell 리스트 조회 성공 - Timetable")
    public void selectAllTimetableCellByTimetable_success() {
        // when
        Optional<Timetable> timetable = timetableRepository.findById(dummyTimetable.getId());   // TODO: QueryDSL 버전

        // then
        assertThat(timetable.isPresent()).isTrue();
        List<TimetableCell> cellList = timetable.get().getCellList();
        assertThat(cellList.size()).isEqualTo(4);
        assertThat(cellList.get(0).getLectureName()).isEqualTo(TimetableCellTemplate.LECTURE_NAME);
        assertThat(cellList.get(1).getLectureName()).isEqualTo("데이터 구조");
        assertThat(cellList.get(2).getLectureName()).isEqualTo("컴퓨터 구조");
        assertThat(cellList.get(3).getLectureName()).isEqualTo("이산 구조");
    }
}
