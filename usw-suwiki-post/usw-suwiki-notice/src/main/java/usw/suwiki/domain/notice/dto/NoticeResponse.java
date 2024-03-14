package usw.suwiki.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeResponse {

  @Data
  public static class Simple {
    private final Long id;
    private final String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedDate;
  }

  @Data
  public static class Detail {
    private final Long id;
    private final String title;
    private final String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedDate;
  }
}
