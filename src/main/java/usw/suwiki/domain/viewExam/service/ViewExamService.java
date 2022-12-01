package usw.suwiki.domain.viewExam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.exam.repository.ViewExamRepository;
import usw.suwiki.domain.lecture.entity.Lecture;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.viewExam.dto.PurchaseHistoryDto;
import usw.suwiki.domain.viewExam.entity.ViewExam;
import usw.suwiki.exception.errortype.AccountException;
import usw.suwiki.exception.ErrorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class ViewExamService {

    private final ViewExamRepository viewExamRepository;
    private final LectureService lectureService;
    private final UserRepository userRepository;

    public void save(Long lectureId, Long userIdx) {     // 연관관계를 맺지 않고 Id 로만 저장 할까 고민중

        Optional<User> user = userRepository.findById(userIdx);
        int point = user.get().getPoint();
        if (point < 20) {
            throw new AccountException(ErrorType.USER_POINT_LACK);
        } else {
            Lecture lecture = lectureService.findById(lectureId);

            ViewExam viewExam = new ViewExam();
            int count = user.get().getViewExamCount();

            user.get().setViewExamCount(count + 1);
            user.get().setPoint(point - 20);
            viewExam.setUserInViewExam(user.get());
            viewExam.setLectureInViewExam(lecture);
            viewExamRepository.save(viewExam);
        }
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
        List<PurchaseHistoryDto> dtoList = new ArrayList<PurchaseHistoryDto>();
        List<ViewExam> list = viewExamRepository.findByUserId(userIdx);
        for (ViewExam viewExam : list) {
            PurchaseHistoryDto dto = PurchaseHistoryDto.builder()
                    .id(viewExam.getId())
                    .lectureName(viewExam.getLecture().getLectureName())
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
