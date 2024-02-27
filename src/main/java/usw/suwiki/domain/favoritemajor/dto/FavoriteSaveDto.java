package usw.suwiki.domain.favoritemajor.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteSaveDto {

    private String majorType;

    public FavoriteSaveDto(String majorType) {
        this.majorType = majorType;
    }
}
