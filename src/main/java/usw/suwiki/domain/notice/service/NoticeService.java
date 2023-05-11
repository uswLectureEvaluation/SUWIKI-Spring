package usw.suwiki.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.notice.controller.dto.NoticeDetailResponseDto;
import usw.suwiki.domain.notice.controller.dto.NoticeResponseDto;
import usw.suwiki.domain.notice.controller.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.domain.notice.domain.Notice;
import usw.suwiki.domain.notice.domain.repository.NoticeRepository;
import usw.suwiki.global.PageOption;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeCRUDService noticeCRUDService;

    public void write(NoticeSaveOrUpdateDto dto) {
        Notice notice = new Notice(dto);
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
        NoticeDetailResponseDto response = new NoticeDetailResponseDto(notice);
        return response;
    }

    public void update(NoticeSaveOrUpdateDto dto, Long noticeId) {
        Notice notice = noticeCRUDService.loadNoticeFromId(noticeId);
        notice.update(dto);
    }

    public void delete(Long noticeId) {
        Notice notice = noticeCRUDService.loadNoticeFromId(noticeId);
        noticeCRUDService.deleteNotice(notice);
    }

}
