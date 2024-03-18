package usw.suwiki.domain.lecture.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@RequiredArgsConstructor
public class LectureSearchOption { // todo: apply validation on controller
  @NotBlank
  private final String orderOption;

  @PositiveOrZero
  private final Integer pageNumber;

  @NotBlank
  private final String majorType;

  public boolean passMajorFiltering() {
    return majorType.equals("전체");
  }
}
