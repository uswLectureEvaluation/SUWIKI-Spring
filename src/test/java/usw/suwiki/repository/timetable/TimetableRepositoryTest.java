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
import org.springframework.dao.DataIntegrityViolationException;
import usw.suwiki.config.TestJpaConfig;
import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.entity.TimetableDay;
import usw.suwiki.domain.timetable.entity.TimetableElement;
import usw.suwiki.domain.timetable.repository.TimetableCellRepository;
import usw.suwiki.domain.timetable.repository.TimetableElementRepository;
import usw.suwiki.domain.timetable.repository.TimetableRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.TimetableException;
import usw.suwiki.template.timetable.TimetableTemplate;
import usw.suwiki.template.timetablecell.TimetableCellTemplate;
import usw.suwiki.template.timetableelement.TimetableElementTemplate;
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
    private TimetableElementRepository timetableElementRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User dummyUser;
    private Timetable dummyTimetable;
    private TimetableCell dummyTimetableCell;
    private TimetableElement dummyTimetableElement;

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

        TimetableElement timetableElement = TimetableElementTemplate.createFirstDummy(dummyTimetable, dummyTimetableCell);
        this.dummyTimetableElement = timetableElementRepository.save(timetableElement);
        TimetableElementTemplate.createDummy("IT 305", TimetableDay.MON, 4, dummyTimetable, dummyTimetableCell);
        TimetableElementTemplate.createDummy("IT 305", TimetableDay.MON, 5, dummyTimetable, dummyTimetableCell);
        TimetableElementTemplate.createDummy("IT 305", TimetableDay.MON, 6, dummyTimetable, dummyTimetableCell);
        timetableCellRepository.save(dummyTimetableCell);

        entityManager.clear();  // 영속성 컨텍스트 초기화
    }

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

    // TODO: 연관관계 메서드를 이용한 삭제 구현 고민

    /**
     * TimetableCell
     */
    @Test
    @DisplayName("TimetableCell 삽입 성공 - 연관관계 엔티티에서 조회가 가능해야 한다.")
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
    @DisplayName("TimetableCell 삽입 실패 - NOT NULL 제약조건을 지켜야 한다.")
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

    /**
     * TimetableElement
     */
    @Test
    @DisplayName("TimetableElement 삽입 성공 - 연관관계 엔티티에서 조회가 가능해야 한다.")
    public void insertTimetableElement_success() {
        // given
        TimetableElement elementA = TimetableElement.builder()
                .location("IT 105")
                .day(TimetableDay.WED)
                .period(1)
                .build();
        elementA.associateTimetableCell(dummyTimetableCell);

        TimetableElement elementB = TimetableElement.builder()
                .location("IT 204")
                .day(TimetableDay.WED)
                .period(2)
                .build();
        elementB.associateTimetableCell(dummyTimetableCell);
        timetableElementRepository.save(elementA);
        timetableElementRepository.save(elementB);

        // when
        TimetableCell foundCell = entityManager.find(TimetableCell.class, dummyTimetableCell.getId());
        Optional<TimetableElement> optionalElementA = timetableElementRepository.findById(elementA.getId());
        Optional<TimetableElement> optionalElementB = timetableElementRepository.findById(elementB.getId());

        // then
        assertThat(optionalElementA.isPresent() && optionalElementB.isPresent()).isTrue();
        assertThat(foundCell.getElementList()).contains(optionalElementA.get(), optionalElementB.get());
    }

    @Test
    @DisplayName("TimetableElement 삽입 실패 - NOT NULL 제약조건을 지켜야 한다.")
    public void insertTimetableElement_fail_notnull_constraint() {
        // given
        TimetableElement nullLocationElement = TimetableElement.builder()
                .location(null)
                .day(TimetableDay.FRI)
                .period(9)
                .build();
        nullLocationElement.associateTimetableCell(dummyTimetableCell);

        TimetableElement nullDayElement = TimetableElement.builder()
                .location("")
                .day(null)
                .period(9)
                .build();
        nullDayElement.associateTimetableCell(dummyTimetableCell);

        // when & then
        assertThatThrownBy(() -> timetableElementRepository.save(nullLocationElement))
                .isExactlyInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> timetableElementRepository.save(nullDayElement))
                .isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("TimetableElement 삽입 실패 - (시간표, 요일, 교시)는 중복되어선 안 된다.")
    public void insertTimetableElement_fail_unique_constraint() {
        // given
        int samePeriod = 1;
        TimetableDay sameDay = TimetableDay.WED;
        TimetableCell sameCell = dummyTimetableCell;

        TimetableElement elementA = TimetableElement.builder()
                .location("IT 105")
                .day(sameDay)
                .period(samePeriod)
                .build();
        elementA.associateTimetableCell(sameCell);

        TimetableElement elementB = TimetableElement.builder()
                .location("IT 204")
                .day(sameDay)
                .period(samePeriod)
                .build();
        elementB.associateTimetableCell(sameCell);

        // when & then
        assertThatThrownBy(() -> timetableCellRepository.save(dummyTimetableCell))
                .isExactlyInstanceOf(DataIntegrityViolationException.class)
                .hasStackTraceContaining("Unique");
        assertThatThrownBy(() -> dummyTimetable.validateElementDayAndPeriodDuplication(elementA))
                .isExactlyInstanceOf(TimetableException.class)
                .hasMessage(ExceptionType.DUPLICATE_TIMETABLE_ELEMENT_DAY_PERIOD.getMessage());
    }

    @Test
    @DisplayName("TimetableElement 단일 조회 성공 - 필드 값이 동등해야 한다.")
    public void selectTimetableElement_success() {
        // given
        Long id = dummyTimetableElement.getId();

        // when
        Optional<TimetableElement> optionalTimetableElement = timetableElementRepository.findById(id);

        // then
        assertThat(optionalTimetableElement.isPresent()).isTrue();
        assertThat(optionalTimetableElement.get().getCell().getId()).isEqualTo(dummyTimetableElement.getCell().getId());
        assertThat(optionalTimetableElement.get().getLocation()).isEqualTo(dummyTimetableElement.getLocation());
        assertThat(optionalTimetableElement.get().getDay()).isEqualTo(dummyTimetableElement.getDay());
    }

    @Test
    @DisplayName("TimetableElement 리스트 조회 성공 - 개수 및 순서가 같아야 한다.")
    public void selectAllTimetableElement_success() {
        // when
        // TODO: QueryDSL 버전
        Optional<TimetableCell> optionalTimetableCell = timetableCellRepository.findById(dummyTimetableCell.getId());

        // then
        assertThat(optionalTimetableCell.isPresent()).isTrue();
        List<TimetableElement> elementList = optionalTimetableCell.get().getElementList();
        assertThat(elementList.size()).isEqualTo(4);
        assertThat(elementList.get(0).getPeriod()).isEqualTo(TimetableElementTemplate.PERIOD);
        assertThat(elementList.get(1).getPeriod()).isEqualTo(4);
        assertThat(elementList.get(2).getPeriod()).isEqualTo(5);
        assertThat(elementList.get(3).getPeriod()).isEqualTo(6);
    }
}