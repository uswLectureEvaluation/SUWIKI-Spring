package usw.suwiki.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.domain.notice.Notice;
import usw.suwiki.domain.notice.controller.dto.NoticeDetailResponseDto;
import usw.suwiki.domain.notice.controller.dto.NoticeResponseDto;
import usw.suwiki.domain.notice.controller.dto.NoticeSaveOrUpdateDto;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeCRUDService noticeCRUDService;

    @Transactional
    public void write(NoticeSaveOrUpdateDto noticeSaveOrUpdateDto) {
        Notice notice = new Notice(noticeSaveOrUpdateDto);
        noticeCRUDService.save(notice);
    }

    public List<NoticeResponseDto> readAllNotice(PageOption option) {
        List<NoticeResponseDto> response = new ArrayList<>();

        List<Notice> notices = noticeCRUDService.loadNotices(option);

        for (Notice notice : notices) {
            response.add(new NoticeResponseDto(notice));
        }
        return response;
    }

    public NoticeDetailResponseDto readNotice(Long noticeId) {
        Notice notice = noticeCRUDService.loadNoticeFromId(noticeId);
      return new NoticeDetailResponseDto(notice);
    }

    @Transactional
    public void update(NoticeSaveOrUpdateDto noticeSaveOrUpdateDto, Long noticeId) {
        Notice notice = noticeCRUDService.loadNoticeFromId(noticeId);
        notice.update(noticeSaveOrUpdateDto);
    }

    @Transactional
    public void delete(Long noticeId) {
        Notice notice = noticeCRUDService.loadNoticeFromId(noticeId);
        noticeCRUDService.deleteNotice(notice);
    }
}
