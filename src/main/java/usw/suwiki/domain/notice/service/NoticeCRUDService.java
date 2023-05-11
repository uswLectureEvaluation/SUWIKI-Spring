package usw.suwiki.domain.notice.service;

import static usw.suwiki.global.exception.ExceptionType.*;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import usw.suwiki.domain.notice.domain.Notice;
import usw.suwiki.domain.notice.domain.repository.NoticeRepository;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.errortype.NoticeException;

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
