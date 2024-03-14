package usw.suwiki.domain.user.viewexam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewExamResponse {

  @Getter
  public static class PurchaseHistory {
    private final Long id;
    private final String professor;
    private final String lectureName;
    private final String majorType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createDate;

    @QueryProjection
    public PurchaseHistory(Long id, String professor, String lectureName, String majorType, LocalDateTime createDate) {
      this.id = id;
      this.professor = professor;
      this.lectureName = lectureName;
      this.majorType = majorType;
      this.createDate = createDate;
    }
  }
}
