package usw.suwiki.repository.timetablecell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
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
import usw.suwiki.template.user.UserTemplate;

@DataJpaTest
@Import(TestJpaConfig.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TimetableCellRepositoryTest {
    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimetableCellRepository timetableCellRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User dummyUser;
    private Timetable dummyTimetable;
    private TimetableCell dummyTimetableCell;

    @BeforeEach
    void setUp() {
        this.dummyUser = userRepository.save(UserTemplate.createDummyUser());

        Timetable timetable = Timetable.builder()
                .name("내 시간표")
                .year(2023)
                .semester(Semester.SECOND)
                .build();
        timetable.associateUser(dummyUser);

        // cell 3개 생성
        TimetableCell cell1 = TimetableCell.builder()
                .lectureName("데이터 구조")
                .professorName("손수국")
                .color(TimetableCellColor.ORANGE)
                .build();
        cell1.associateTimetable(timetable);
        TimetableCell cell2 = TimetableCell.builder()
                .lectureName("컴퓨터 구조")
                .professorName("갓성태")
                .color(TimetableCellColor.ORANGE)
                .build();
        cell2.associateTimetable(timetable);
        TimetableCell cell3 = TimetableCell.builder()
                .lectureName("이산 구조")
                .professorName("김장영")
                .color(TimetableCellColor.ORANGE)
                .build();
        cell3.associateTimetable(timetable);

        this.dummyTimetable = timetableRepository.save(timetable);
    }

    // TimetableCell 테스트
    @Test
    @DisplayName("INSERT TimetableCell 성공 - Timetable 연관관계 편의 메서드")
    public void insertTimetableCell_success() {
        // given
        TimetableCell timetableCell = TimetableCell.builder()
                .lectureName("")        // blank 가능
                .professorName("")      // blank 가능
                .color(TimetableCellColor.BROWN)
                .build();

        // when
        timetableCell.associateTimetable(dummyTimetable);   // 연관관계 편의 메서드
        entityManager.persist(dummyTimetable);
        entityManager.flush();
        entityManager.clear();

        Timetable foundTable = entityManager.find(Timetable.class, dummyTimetable.getId());
        TimetableCell foundCell = entityManager.find(TimetableCell.class, timetableCell.getId());

        // then
        assertThat(foundCell.getId()).isEqualTo(timetableCell.getId());
        assertThat(foundCell.getTimetable()).isEqualTo(foundTable);
    }

    @Test
    @DisplayName("INSERT TimetableCell 실패 - NOT NULL 제약조건 위반")
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
    @DisplayName("SELECT ALL TimetableCell by Timetable id 성공")
    public void selectAllTimetableCellByTimetableId_success() {
        // when
        List<TimetableCell> all = timetableCellRepository.findAllByTimetableId(dummyTimetable.getId());

        // then
        assertThat(all.isEmpty()).isFalse();
        assertThat(all.size()).isEqualTo(3);
    }
}
