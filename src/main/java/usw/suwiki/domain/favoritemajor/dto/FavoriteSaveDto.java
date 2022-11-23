package usw.suwiki.domain.favoritemajor.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  //noArgsConstructor 데이터 삽입
public class FavoriteSaveDto {
    private String majorType;

    public FavoriteSaveDto(String majorType) {
        this.majorType = majorType;
    }
}
