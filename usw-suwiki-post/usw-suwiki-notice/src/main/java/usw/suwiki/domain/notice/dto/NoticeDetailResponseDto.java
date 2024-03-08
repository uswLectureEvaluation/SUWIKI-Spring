package usw.suwiki.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import usw.suwiki.domain.notice.Notice;

import java.time.LocalDateTime;

@Getter
public class NoticeDetailResponseDto {

    private Long id;
    private String title;

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        timezone = "Asia/Seoul"
    )
    private LocalDateTime modifiedDate;
    private String content;

    public NoticeDetailResponseDto(Notice entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.modifiedDate = entity.getModifiedDate();
        this.content = entity.getContent();
    }
}

