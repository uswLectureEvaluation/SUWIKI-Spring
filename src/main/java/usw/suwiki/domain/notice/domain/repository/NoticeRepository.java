package usw.suwiki.domain.notice.domain.repository;

import java.util.List;
import usw.suwiki.domain.notice.domain.Notice;
import usw.suwiki.global.PageOption;

public interface NoticeRepository {

    void save(Notice notice);

    Notice findById(Long id);

    List<Notice> findByNoticeList(PageOption page);

    void delete(Notice notice);
}
