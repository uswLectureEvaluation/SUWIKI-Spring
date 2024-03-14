package usw.suwiki.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.NoticeException;
import usw.suwiki.domain.notice.Notice;
import usw.suwiki.domain.notice.NoticeRepository;
import usw.suwiki.domain.notice.dto.NoticeResponse;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public void write(String title, String content) {
        Notice notice = new Notice(title, content);
        noticeRepository.save(notice);
    }

    public List<NoticeResponse.Simple> getAllNotices(PageOption option) {
        return noticeRepository.findByNoticeList(option).stream()
          .map(notice -> new NoticeResponse.Simple(notice.getId(), notice.getTitle(), notice.getModifiedDate()))
          .toList();
    }

    public NoticeResponse.Detail getNotice(Long noticeId) {
        Notice notice = findNoticeById(noticeId);

        return new NoticeResponse.Detail(
          notice.getId(),
          notice.getTitle(),
          notice.getContent(),
          notice.getModifiedDate()
        );
    }

    @Transactional
    public void update(Long noticeId, String title, String content) {
        Notice notice = findNoticeById(noticeId);
        notice.update(title, content);
    }

    @Transactional
    public void delete(Long noticeId) {
        Notice notice = findNoticeById(noticeId);
        noticeRepository.delete(notice);
    }

    private Notice findNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId);

        if (notice == null) {
            throw new NoticeException(ExceptionType.NOTICE_NOT_FOUND);
        }

        return notice;
    }
}
