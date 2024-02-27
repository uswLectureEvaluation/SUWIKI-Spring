package usw.suwiki.domain.notice.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.notice.domain.Notice;

@Getter
@NoArgsConstructor
public class NoticeResponseDto {

    private Long id;
    private String title;
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        timezone = "Asia/Seoul"
    )
    private LocalDateTime modifiedDate;

    public NoticeResponseDto(Notice entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.modifiedDate = entity.getModifiedDate();
    }
}
