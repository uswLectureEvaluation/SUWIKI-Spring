package usw.suwiki.domain.timetable.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.global.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableCell extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_cell_id")
    private Long id;

    // (lectureName, professorName) 중복 가능
    @NotNull
    @Size(max = 200)
    private String lectureName;     // blank 가능

    @NotNull
    @Size(max = 100)
    private String professorName;   // blank 가능

    @Enumerated(EnumType.STRING)
    @NotNull
    private TimetableCellColor color;

    @Valid
    @Embedded
    private TimetableCellSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    // TODO 고민: 오버랩 검증 로직 및 연관관계 편의 메서드 호출을 생성자로 이동
    // timetable을 받게 되면, timetable의 null 여부에 따른 분기 처리를 해야 함. 무조건 검증을 하면 복잡해질 듯
    @Builder
    public TimetableCell(String lectureName, String professorName, TimetableCellColor color,
                         TimetableCellSchedule schedule) {
        this.lectureName = lectureName;
        this.professorName = professorName;
        this.color = color;
        this.schedule = schedule;
    }

    // 연관관계 편의 메서드
    public void associateTimetable(Timetable timetable) {
        if (Objects.nonNull(this.timetable)) {
            this.timetable.removeCell(this);
        }
        this.timetable = timetable;
        timetable.addCell(this);
    }

    public void dissociateTimetable(Timetable timetable) {
        this.timetable = null;
        timetable.removeCell(this);
    }

    public Long bringTimetableId() {
        return this.timetable.getId();
    }

    // 비즈니스 메서드
    public void update(
            String lectureName, String professorName, TimetableCellColor color,
            TimetableCellSchedule schedule
    ) {
        this.lectureName = lectureName;
        this.professorName = professorName;
        this.color = color;
        this.schedule = schedule;
    }

}
