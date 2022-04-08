package usw.suwiki.dto.notice;

import usw.suwiki.domain.notice.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeResponseDto {
    private Long id;
    private String title;
    private LocalDateTime modifiedDate;

    public NoticeResponseDto(Notice entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.modifiedDate = entity.getModifiedDate();
    }
}
