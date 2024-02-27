package usw.suwiki.domain.notice.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.notice.controller.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.global.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
