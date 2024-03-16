package usw.suwiki.domain.notice.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeRequest {

  @Data
  public static class Create {
    @NotBlank
    private final String title;

    @NotBlank
    private final String content;
  }

  @Data
  public static class Update {
    @NotBlank
    private final String title;

    @NotBlank
    private final String content;
  }
}
