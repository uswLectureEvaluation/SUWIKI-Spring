package usw.suwiki.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeSaveOrUpdateDto {
    private String title;
    private String content;
}
