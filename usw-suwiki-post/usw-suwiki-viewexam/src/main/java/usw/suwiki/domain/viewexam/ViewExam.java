package usw.suwiki.domain.viewexam;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewExam extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long lectureId;

  public ViewExam(Long userId, Long lectureId) {
    this.userId = userId;
    this.lectureId = lectureId;
  }
}
