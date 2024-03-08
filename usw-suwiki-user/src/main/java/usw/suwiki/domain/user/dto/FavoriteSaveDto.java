package usw.suwiki.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteSaveDto {
    // todo: no validation?, 필요 없는 DTO
    private String majorType;
}
