package usw.suwiki.domain.notice.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.notice.controller.dto.NoticeDetailResponseDto;
import usw.suwiki.domain.notice.controller.dto.NoticeResponseDto;
import usw.suwiki.domain.notice.controller.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.domain.notice.domain.Notice;
import usw.suwiki.global.PageOption;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeCRUDService noticeCRUDService;

    @Transactional
    public void write(NoticeSaveOrUpdateDto noticeSaveOrUpdateDto) {
        Notice notice = new Notice(noticeSaveOrUpdateDto);
        noticeCRUDService.save(notice);
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseDto> readAllNotice(PageOption option) {
        List<NoticeResponseDto> response = new ArrayList<>();
        List<Notice> notices = noticeCRUDService.loadNotices(option);
        for (Notice notice : notices) {
            response.add(new NoticeResponseDto(notice));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponseDto readNotice(Long noticeId) {
        Notice notice = noticeCRUDService.loadNoticeFromId(noticeId);
        NoticeDetailResponseDto response = new NoticeDetailResponseDto(notice);
        return response;
    }

    @Transactional
    public void update(
        NoticeSaveOrUpdateDto noticeSaveOrUpdateDto,
        Long noticeId
    ) {
        Notice notice = noticeCRUDService.loadNoticeFromId(noticeId);
        notice.update(noticeSaveOrUpdateDto);
    }


    @Transactional
    public void delete(Long noticeId) {
        Notice notice = noticeCRUDService.loadNoticeFromId(noticeId);
        noticeCRUDService.deleteNotice(notice);
    }
}
