package usw.suwiki.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.core.exception.errortype.NoticeException;
import usw.suwiki.domain.notice.Notice;
import usw.suwiki.domain.notice.NoticeRepository;
import usw.suwiki.global.PageOption;

import java.util.List;

import static usw.suwiki.global.exception.ExceptionType.NOTICE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NoticeCRUDService {
    private final NoticeRepository noticeRepository;

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

    public void deleteNotice(Notice notice) {
        noticeRepository.delete(notice);
    }

    public void validateNotNull(Notice notice) {
        if (notice == null) {
            throw new NoticeException(NOTICE_NOT_FOUND);
        }
    }
}
