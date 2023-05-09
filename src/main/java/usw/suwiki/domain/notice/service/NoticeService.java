package usw.suwiki.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.notice.dto.NoticeDetailResponseDto;
import usw.suwiki.domain.notice.dto.NoticeResponseDto;
import usw.suwiki.domain.notice.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.domain.notice.Notice;
import usw.suwiki.domain.notice.repository.NoticeRepository;
import usw.suwiki.global.PageOption;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public void save(NoticeSaveOrUpdateDto dto) {
        Notice notice = new Notice(dto);
        noticeRepository.save(notice);
    }

    public List<NoticeResponseDto> findNoticeList(PageOption page) {
        List<NoticeResponseDto> dtoList = new ArrayList<>();
        List<Notice> list = noticeRepository.findByNoticeList(page);
        for (Notice notice : list) {
            dtoList.add(new NoticeResponseDto(notice));
        }
        return dtoList;
    }

    public NoticeDetailResponseDto findNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId);
        NoticeDetailResponseDto dto = new NoticeDetailResponseDto(notice);
        return dto;
    }

    public void update(NoticeSaveOrUpdateDto dto, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId);
        notice.update(dto);
    }

    public void delete(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId);
        noticeRepository.delete(notice);
    }

}
