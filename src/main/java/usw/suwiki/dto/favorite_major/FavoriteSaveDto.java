package usw.suwiki.dto.favorite_major;

import lombok.Getter;

@Getter
public class FavoriteSaveDto {
    private String majorType;

    public FavoriteSaveDto(String majorType) {
        this.majorType = majorType;
    }
}
