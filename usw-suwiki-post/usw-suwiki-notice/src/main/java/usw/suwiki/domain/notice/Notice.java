package usw.suwiki.domain.notice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.notice.controller.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
