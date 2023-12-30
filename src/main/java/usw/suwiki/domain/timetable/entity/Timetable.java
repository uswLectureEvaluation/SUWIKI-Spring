package usw.suwiki.domain.timetable.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.BaseTimeEntity;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.TimetableException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timetable extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Size(max = 200)
    private String name;    // 중복 가능 (UNIQUE 제약 조건 없음)

    @NotNull
    @Min(value = 1023)
    @Max(value = 3023)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Semester semester;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimetableCell> cellList = new ArrayList<>();

    @Builder
    public Timetable(String name, Integer year, Semester semester) {
        this.name = name;
        this.year = year;
        this.semester = semester;
    }

    // 연관관계 메서드
    public void associateUser(User user) {
        if (Objects.nonNull(this.user)) {
            this.user.removeTimetable(this);
        }
        this.user = user;
        user.addTimetable(this);
    }

    public void addCell(TimetableCell cell) {
        this.cellList.add(cell);
    }

    public void removeCell(TimetableCell cell) {
        this.cellList.remove(cell);
    }

    // 비즈니스 메서드
    public void validateCellDayAndPeriodsDuplication(TimetableCell cell) {
        if (cellList.stream().anyMatch(it -> it.hasDayAndPeriodsDuplication(cell))) {
            throw new TimetableException(ExceptionType.DUPLICATE_TIMETABLE_DAY_PERIOD);
        }
    }

    public void update(String name, Integer year, Semester semester) {
        this.name = name;
        this.year = year;
        this.semester = semester;
    }

    public void validateIsAuthor(User user) {
        if (!isAuthorId(user.getId())) {
            throw new TimetableException(ExceptionType.TIMETABLE_NOT_AN_AUTHOR);
        }
    }

    private boolean isAuthorId(Long userId) {
        return this.user.getId().equals(userId);
    }
}
