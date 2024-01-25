package usw.suwiki.domain.timetable.repository;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.entity.TimetableCellSchedule;
import usw.suwiki.domain.timetable.entity.TimetableDay;
import usw.suwiki.domain.timetable.template.TimetableCellTemplate;
import usw.suwiki.domain.timetable.template.TimetableTemplate;
import usw.suwiki.domain.user.template.UserTemplate;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.annotation.SuwikiJpaTest;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.TimetableException;

@SuwikiJpaTest
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
        TimetableCellTemplate.createDummy(dummyTimetable, "데이터 구조", "손수국", GRAY, "IT 305", TimetableDay.TUE, 1, 3);
        TimetableCellTemplate.createDummy(dummyTimetable, "컴퓨터 구조", "갓성태", ORANGE, "IT 504", TimetableDay.WED, 1, 3);
        TimetableCellTemplate.createDummy(dummyTimetable, "이산 구조", "김장영", BROWN, "IT 105", TimetableDay.THU, 1, 3);
        timetableRepository.save(dummyTimetable);

        entityManager.clear();  // 영속성 컨텍스트 초기화
    }

//    @AfterEach
//    void tearDown() {
//        timetableRepository.deleteAll();
//        timetableCellRepository.deleteAll();
//        userRepository.deleteAll();
//    }

    /**
     * Timetable
     */
    @Test
    @DisplayName("Timetable 삽입 성공 - 연관관계 엔티티에서 조회가 가능해야 한다.")
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
        assertThat(foundUser.getTimetableList()).contains(foundTable.get());
    }

    @Test
    @DisplayName("Timetable 삽입 실패 - NOT NULL 제약조건을 지켜야 한다.")
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
    @DisplayName("Timetable 삽입 실패 - 값 범위 제약조건을 지켜야 한다.")
    public void insertTimetable_fail_value_range_constraint() {
        // given
        Timetable invalidYearTable = Timetable.builder()
                .name("임진왜란보다 천년 전")
                .year(592)
                .semester(Semester.FIRST)
                .build();
        invalidYearTable.associateUser(dummyUser);

        Timetable invalidNameTable = Timetable.builder()
                .name("a".repeat(201))
                .year(2023)
                .semester(Semester.FIRST)
                .build();
        invalidNameTable.associateUser(dummyUser);

        // when & then
        assertThatThrownBy(() -> timetableRepository.save(invalidYearTable))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableRepository.save(invalidNameTable))
                .isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Timetable 단일 조회 성공 - 필드 값이 동등해야 한다.")
    public void selectTimetableById_success() {
        // given
        Long id = dummyTimetable.getId();

        // when
        Optional<Timetable> optionalTimetable = timetableRepository.findById(id);

        // then
        assertThat(optionalTimetable.isPresent()).isTrue();
        assertThat(optionalTimetable.get().getUser().getId()).isEqualTo(dummyTimetable.getUser().getId());
        assertThat(optionalTimetable.get().getSemester()).isEqualTo(dummyTimetable.getSemester());
    }

    @Test
    @DisplayName("Timetable 리스트 조회 성공 - 개수 및 순서가 같아야 한다.")
    public void selectAllTimetable_success() {
        // given
        Long userId = dummyUser.getId();

        // when
        List<Timetable> all = timetableRepository.findAllByUserId(userId);

        // then
        assertThat(all.isEmpty()).isFalse();
        assertThat(all.size()).isEqualTo(4);
        assertThat(all.get(0).getYear()).isEqualTo(TimetableTemplate.YEAR);
        assertThat(all.get(1).getYear()).isEqualTo(2017);
        assertThat(all.get(2).getYear()).isEqualTo(2017);
        assertThat(all.get(3).getYear()).isEqualTo(2018);
    }

    /**
     * TimetableCell
     */
    @Test
    @DisplayName("TimetableCell 삽입 성공 - 연관관계 엔티티에서 조회가 가능해야 한다.")
    public void insertTimetableCell_success() {
        // given
        TimetableCellSchedule schedule = TimetableCellSchedule.builder()
                .location("글경 603")
                .day(TimetableDay.FRI)
                .startPeriod(null)      // null 가능
                .endPeriod(null)        // null 가능
                .build();
        TimetableCell timetableCell = TimetableCell.builder()
                .lectureName("")        // blank 가능
                .professorName("")      // blank 가능
                .color(TimetableCellColor.BROWN)
                .schedule(schedule)
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
    @DisplayName("TimetableCell 삽입 실패 - NOT NULL 제약조건을 지켜야 한다.")
    public void insertTimetableCell_fail_notnull_constraint() {
        // given
        TimetableCellSchedule dummySchedule = TimetableCellSchedule.builder()
                .location("글경 603")
                .day(TimetableDay.FRI)
                .build();
        TimetableCellSchedule nullLocationSchedule = TimetableCellSchedule.builder()
                .location(null)
                .day(TimetableDay.FRI)
                .build();
        TimetableCellSchedule nullDaySchedule = TimetableCellSchedule.builder()
                .location("글경 603")
                .day(null)
                .build();

        TimetableCell nullLectureNameCell = TimetableCell.builder()
                .lectureName(null)
                .professorName("신호진")
                .color(TimetableCellColor.BROWN)
                .schedule(dummySchedule)
                .build();
        nullLectureNameCell.associateTimetable(dummyTimetable);

        TimetableCell nullProfessorNameCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName(null)
                .color(TimetableCellColor.BROWN)
                .schedule(dummySchedule)
                .build();
        nullProfessorNameCell.associateTimetable(dummyTimetable);

        TimetableCell nullColorCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName("신호진")
                .color(null)
                .schedule(dummySchedule)
                .build();
        nullColorCell.associateTimetable(dummyTimetable);

        TimetableCell nullLocationCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName("신호진")
                .color(TimetableCellColor.BROWN)
                .schedule(nullLocationSchedule)
                .build();
        nullLocationCell.associateTimetable(dummyTimetable);

        TimetableCell nullDayCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName("신호진")
                .color(TimetableCellColor.BROWN)
                .schedule(nullDaySchedule)
                .build();
        nullDayCell.associateTimetable(dummyTimetable);

        // when & then
        assertThatThrownBy(() -> timetableCellRepository.save(nullLectureNameCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(nullProfessorNameCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(nullColorCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(nullLocationCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(nullDayCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
    }


    @Test
    @DisplayName("TimetableCell 삽입 실패 - 값 범위 제약조건을 지켜야 한다.")
    public void insertTimetableCell_fail_value_range_constraint() {
        // given

        TimetableCellSchedule dummySchedule = TimetableCellSchedule.builder()
                .location("글경 603")
                .day(TimetableDay.FRI)
                .build();
        TimetableCellSchedule tooLongLocationSchedule = TimetableCellSchedule.builder()
                .location("a".repeat(201))
                .day(TimetableDay.FRI)
                .build();
        TimetableCellSchedule tooBigPeriodSchedule = TimetableCellSchedule.builder()
                .location("글경 603")
                .day(TimetableDay.FRI)
                .startPeriod(25)
                .build();

        TimetableCell tooLongLectureNameCell = TimetableCell.builder()
                .lectureName("a".repeat(201))
                .professorName("신호진")
                .color(TimetableCellColor.BROWN)
                .schedule(dummySchedule)
                .build();
        tooLongLectureNameCell.associateTimetable(dummyTimetable);

        TimetableCell tooLongProfessorNameCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName("a".repeat(101))
                .color(TimetableCellColor.BROWN)
                .schedule(dummySchedule)
                .build();
        tooLongProfessorNameCell.associateTimetable(dummyTimetable);

        TimetableCell tooLongLocationCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName("신호진")
                .color(TimetableCellColor.BROWN)
                .schedule(tooLongLocationSchedule)
                .build();
        tooLongLocationCell.associateTimetable(dummyTimetable);

        TimetableCell tooBigPeriodCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName("신호진")
                .color(TimetableCellColor.BROWN)
                .schedule(tooBigPeriodSchedule)
                .build();
        tooLongLocationCell.associateTimetable(dummyTimetable);

        // when & then
        assertThatThrownBy(() -> timetableCellRepository.save(tooLongLectureNameCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(tooLongProfessorNameCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(tooLongLocationCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableCellRepository.save(tooBigPeriodCell))
                .isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("TimetableCell 삽입 실패 - 기존 시간표 셀들과 겹치는 (요일, 교시)을 가져선 안 된다.")
    public void insertTimetableCell_fail_duplicate_day_and_period() {
        // given
        TimetableCellSchedule scheduleA = TimetableCellSchedule.builder()
                .location("IT 208")
                .day(TimetableDay.FRI)
                .startPeriod(4)
                .endPeriod(6)
                .build();

        TimetableCell timetableCell = TimetableCell.builder()
                .lectureName("ICT 개론")
                .professorName("신호진")
                .color(BROWN)
                .schedule(scheduleA)
                .build();
        timetableCell.associateTimetable(dummyTimetable);
        timetableCellRepository.save(timetableCell);

        TimetableCellSchedule scheduleB = TimetableCellSchedule.builder()
                .location("IT 208")
                .day(TimetableDay.FRI)
                .startPeriod(1)
                .endPeriod(5)   // 겹치는 교시
                .build();

        TimetableCellSchedule scheduleC = TimetableCellSchedule.builder()
                .location("IT 208")
                .day(TimetableDay.FRI)
                .startPeriod(6) // 겹치는 교시
                .endPeriod(8)
                .build();

        // when & then
        assertThatThrownBy(() -> dummyTimetable.validateCellScheduleOverlapBeforeAssociation(scheduleB))
                .isExactlyInstanceOf(TimetableException.class)
                .hasMessage(ExceptionType.OVERLAPPED_TIMETABLE_CELL_SCHEDULE.getMessage());
        assertThatThrownBy(() -> dummyTimetable.validateCellScheduleOverlapBeforeAssociation(scheduleC))
                .isExactlyInstanceOf(TimetableException.class)
                .hasMessage(ExceptionType.OVERLAPPED_TIMETABLE_CELL_SCHEDULE.getMessage());
    }


    @Test
    @DisplayName("TimetableCell 단일 조회 성공 - 필드 값이 동등해야 한다.")
    public void selectTimetableCell_success() {
        // given
        Long id = dummyTimetableCell.getId();

        // when
        Optional<TimetableCell> optionalTimetableCell = timetableCellRepository.findById(id);

        // then
        assertThat(optionalTimetableCell.isPresent()).isTrue();
        assertThat(optionalTimetableCell.get().getTimetable().getId()).isEqualTo(
                dummyTimetableCell.getTimetable().getId());
        assertThat(optionalTimetableCell.get().getLectureName()).isEqualTo(dummyTimetableCell.getLectureName());
        assertThat(optionalTimetableCell.get().getProfessorName()).isEqualTo(dummyTimetableCell.getProfessorName());
    }

    @Test
    @DisplayName("TimetableCell 리스트 조회 성공 - 개수 및 순서가 같아야 한다.")
    public void selectAllTimetableCell_success() {
        // when
        Optional<Timetable> timetable = timetableRepository.findById(dummyTimetable.getId());

        // then
        assertThat(timetable.isPresent()).isTrue();
        List<TimetableCell> cellList = timetable.get().getCellList();
        assertThat(cellList.size()).isEqualTo(4);
        assertThat(cellList.get(0).getLectureName()).isEqualTo(TimetableCellTemplate.LECTURE_NAME_A);
        assertThat(cellList.get(1).getLectureName()).isEqualTo("데이터 구조");
        assertThat(cellList.get(2).getLectureName()).isEqualTo("컴퓨터 구조");
        assertThat(cellList.get(3).getLectureName()).isEqualTo("이산 구조");
    }

}
