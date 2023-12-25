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
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.BaseTimeEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timetable extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String name;

    @NotNull
    private Integer year;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Semester semester;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TimetableCell> cellList = new ArrayList<>();


    // 연관관계 메서드
    public void associateUser(User user) {
        System.out.println("Objects.nonNull(this.user) = " + Objects.nonNull(this.user));
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

}
