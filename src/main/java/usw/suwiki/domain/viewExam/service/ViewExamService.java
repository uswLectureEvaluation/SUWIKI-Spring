package usw.suwiki.domain.viewExam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.viewExam.repository.ViewExamRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.user.entity.User;
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

    public void save(Long lectureId, Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_EXISTS));
        if (user.getPoint() < 20) {
            throw new AccountException(ExceptionType.USER_POINT_LACK);
        }
        Lecture lecture = lectureService.findById(lectureId);
        userRepository.updateViewExamCount(user.getId(), user.getViewExamCount() + 1);
        userRepository.updatePoint(user.getId(), (user.getPoint() + 20));
        ViewExam viewExam = new ViewExam();
        viewExam.setUserInViewExam(user);
        viewExam.setLectureInViewExam(lecture);
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

    public void deleteByUserIdx(Long userIdx) {
        List<ViewExam> list = viewExamRepository.findByUserId(userIdx);
        for (ViewExam viewExam : list) {
            viewExamRepository.delete(viewExam);
        }
    }
}
