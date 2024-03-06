package usw.suwiki.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.NoticeException;
import usw.suwiki.domain.notice.Notice;
import usw.suwiki.domain.notice.NoticeRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeCRUDService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public void save(Notice notice) {
        noticeRepository.save(notice);
    }

    public List<Notice> loadNotices(PageOption page) {
        return noticeRepository.findByNoticeList(page);
    }

    public Notice loadNoticeFromId(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId);
        validateNotNull(notice);

        return notice;
    }

    @Transactional
    public void deleteNotice(Notice notice) {
        noticeRepository.delete(notice);
    }

    public void validateNotNull(Notice notice) {
        if (notice == null) {
            throw new NoticeException(ExceptionType.NOTICE_NOT_FOUND);
        }
    }
}
