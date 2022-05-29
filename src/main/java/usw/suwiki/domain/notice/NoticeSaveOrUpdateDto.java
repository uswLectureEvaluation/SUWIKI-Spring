package usw.suwiki.domain.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeSaveOrUpdateDto {
    private String title;
    private String content;

    public NoticeSaveOrUpdateDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
