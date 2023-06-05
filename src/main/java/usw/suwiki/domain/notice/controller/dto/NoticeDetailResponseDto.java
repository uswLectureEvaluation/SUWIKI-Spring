package usw.suwiki.domain.notice.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.notice.domain.Notice;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
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

