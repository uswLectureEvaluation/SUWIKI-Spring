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

    @NotNull
    private String lectureName;     // blank 가능

    @NotNull
    private String professorName;   // blank 가능

    @Enumerated(EnumType.STRING)
    @NotNull
    private TimetableCellColor color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @OneToMany(mappedBy = "cell", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimetableElement> elementList = new ArrayList<>();


    // 연관관계 편의 메서드
    public void associateTimetable(Timetable timetable) {
        if (Objects.nonNull(this.timetable)) {
            this.timetable.removeCell(this);
        }
        this.timetable = timetable;
        timetable.addCell(this);
    }

    public void addElement(TimetableElement element) {
        this.elementList.add(element);
    }

    public void removeElement(TimetableElement element) {
        this.elementList.remove(element);
    }

    // 비즈니스 메서드
    //

}
