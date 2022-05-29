package usw.suwiki.domain.notice;

import usw.suwiki.global.PageOption;
import usw.suwiki.domain.notice.Notice;

import java.util.List;

public interface NoticeRepository {
    void save(Notice notice);

    Notice findById(Long id);

    List<Notice> findByNoticeList(PageOption page);

    void delete(Notice notice);
}
