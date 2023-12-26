package usw.suwiki.domain.timetable.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.global.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UNIQUE_CELL_DAY_PERIOD", columnNames = {"timetable_cell_id", "day", "period"})
        }
)
public class TimetableElement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_element_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_cell_id")
    private TimetableCell cell;

    @NotNull
    private String location;    // blank 가능

    @NotNull
    private TimetableDay day;

    // TODO: 1~10 제약 조건
    private Integer period;

    @Builder
    public TimetableElement(String location, TimetableDay day, Integer period) {
        this.location = location;
        this.day = day;
        this.period = period;
    }


    // 연관관계 메서드
    public void associateTimetableCell(TimetableCell cell) {
        if (Objects.nonNull(this.cell)) {
            this.cell.removeElement(this);
        }
        this.cell = cell;
        cell.addElement(this);
    }

    // 비즈니스 메서드
}
