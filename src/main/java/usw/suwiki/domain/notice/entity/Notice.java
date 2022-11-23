package usw.suwiki.domain.notice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.notice.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.global.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Notice extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @LastModifiedDate // 조회한 Entity값을 변경할때 ,시간이 자동 저장된다.
    private LocalDateTime modifiedDate;

    private String title;
    private String content;

    public Notice(NoticeSaveOrUpdateDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    public void update(NoticeSaveOrUpdateDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }
}
