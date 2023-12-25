package usw.suwiki.domain.timetable.entity;

import java.util.Objects;
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
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.global.BaseTimeEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableCell extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_cell_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @NotNull
    private String lectureName;

    @NotNull
    private String professorName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TimetableCellColor color;

    // 연관관계 편의 메서드
    public void associateTimetable(Timetable timetable) {
        System.out.println("Objects.nonNull(this.timetable) = " + Objects.nonNull(this.timetable));

        if (Objects.nonNull(this.timetable)) {
            this.timetable.removeCell(this);
        }
        this.timetable = timetable;
        timetable.addCell(this);
    }

    // TimetableElement 추가
    // TimetableElement 삭제

    // 비즈니스 메서드
    //

}
