package usw.suwiki.domain.viewExam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.viewExam.repository.ViewExamRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.viewExam.dto.PurchaseHistoryDto;
import usw.suwiki.domain.viewExam.entity.ViewExam;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ViewExamService {

    private final ViewExamRepository viewExamRepository;
    private final LectureService lectureService;
    private final UserRepository userRepository;

    public boolean isExist(Long userId, Long lectureId) {
        return viewExamRepository.validateIsExists(userId, lectureId);
    }

    public void open(Long lectureId, Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_EXISTS));

        Lecture lecture = lectureService.findById(lectureId);
        user.purchaseExamPost();

        ViewExam viewExam = ViewExam.builder()
            .user(user)
            .lecture(lecture)
            .build();
        viewExamRepository.save(viewExam);
    }

    public boolean verifyAuth(Long lectureId, Long userIdx) {
        List<ViewExam> list = viewExamRepository.findByUserId(userIdx);
        for (ViewExam viewExam : list) {
            if (viewExam.getLecture().getId().equals(lectureId)) {
                return true;
            }
        }
        return false;
    }

    public List<PurchaseHistoryDto> findByUserId(Long userIdx) {
        List<PurchaseHistoryDto> dtoList = new ArrayList<>();
        List<ViewExam> list = viewExamRepository.findByUserId(userIdx);
        for (ViewExam viewExam : list) {
            PurchaseHistoryDto dto = PurchaseHistoryDto.builder()
                    .id(viewExam.getId())
                    .lectureName(viewExam.getLecture().getName())
                    .professor(viewExam.getLecture().getProfessor())
                    .majorType(viewExam.getLecture().getMajorType())
                    .createDate(viewExam.getCreateDate())
                    .build();

            dtoList.add(dto);
        }
        return dtoList;
    }

    public void deleteFromUserIdx(Long userIdx) {
        List<ViewExam> list = viewExamRepository.findByUserId(userIdx);
        for (ViewExam viewExam : list) {
            viewExamRepository.delete(viewExam);
        }
    }
}
