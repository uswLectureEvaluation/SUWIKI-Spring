package usw.suwiki.domain.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeDetailResponseDto {
    private Long id;
    private String title;
    private LocalDateTime modifiedDate;
    private String content;

    public NoticeDetailResponseDto(Notice entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.modifiedDate = entity.getModifiedDate();
        this.content = entity.getContent();
    }
}

