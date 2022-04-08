package usw.suwiki.service.notice;

import usw.suwiki.dto.notice.NoticeDetailResponseDto;
import usw.suwiki.dto.notice.NoticeResponseDto;
import usw.suwiki.dto.notice.NoticeSaveOrUpdateDto;
import usw.suwiki.domain.notice.Notice;
import usw.suwiki.repository.notice.JpaNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class NoticeService {
    private final JpaNoticeRepository jpaNoticeRepository;

    public void save(NoticeSaveOrUpdateDto dto){
        Notice notice = new Notice(dto);
        jpaNoticeRepository.save(notice);
    }

    public List<NoticeResponseDto> findNoticeList(){
        List<NoticeResponseDto> dtoList = new ArrayList<>();
        List<Notice> list = jpaNoticeRepository.findByNoticeList();
        for (Notice notice : list) {
            dtoList.add(new NoticeResponseDto(notice));
        }
        return dtoList;
    }

    public NoticeDetailResponseDto findNoticeDetail(Long noticeId){
        Notice notice = jpaNoticeRepository.findById(noticeId);
        NoticeDetailResponseDto dto = new NoticeDetailResponseDto(notice);

        return dto;
    }

    public void update(NoticeSaveOrUpdateDto dto, Long noticeId){
        Notice notice = jpaNoticeRepository.findById(noticeId);
        notice.update(dto);
    }

    public void delete(Long noticeId){
        Notice notice = jpaNoticeRepository.findById(noticeId);
        jpaNoticeRepository.delete(notice);
    }

}
