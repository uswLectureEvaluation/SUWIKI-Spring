package usw.suwiki.domain.lecture.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureSchedule extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long lectureId;

  @Column(name = "place_schedule", nullable = false)
  private String placeSchedule;  // 장소와 시간

  @Column(nullable = false, updatable = false)
  private String semester;

  public LectureSchedule(Long lectureId, String placeSchedule, String semester) {
    this.lectureId = lectureId;
    this.placeSchedule = placeSchedule;
    this.semester = semester;
  }
}
