package usw.suwiki.domain.lecture.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_schedule", nullable = false)
    private String placeSchedule;

    @Column(nullable = false, updatable = false)
    private String semester;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lecture lecture;

    @Builder
    public LectureSchedule(String placeSchedule, String semester, Lecture lecture) {
        this.placeSchedule = placeSchedule;
        this.semester = semester;
        associateLecture(lecture);  // TODO refactor: Lecture - addSchedule 메서드로 책임 분리
    }

    //-------------------------------------------------------------------------
    // 연관관계 메서드
    //-------------------------------------------------------------------------
    private void associateLecture(Lecture lecture) {
        if (Objects.nonNull(this.lecture)) {
            this.lecture.removeSchedule(this);
        }
        this.lecture = lecture;
        lecture.addSchedule(this);
    }

    //-------------------------------------------------------------------------
    // 비즈니스 메서드
    //-------------------------------------------------------------------------


}
