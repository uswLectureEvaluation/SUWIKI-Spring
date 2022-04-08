package usw.suwiki.service.viewExam;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.viewExam.ViewExam;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.repository.user.UserRepository;
import usw.suwiki.repository.viewExam.JpaViewExamRepository;
import usw.suwiki.service.lecture.LectureService;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class ViewExamService {

    private final JpaViewExamRepository jpaViewExamRepository;
    private final LectureService lectureService;
    private final UserRepository userRepository;

    public void save(Long lectureId, Long userIdx){     // 연관관계를 맺지 않고 Id 로만 저장 할까 고민중

        Optional<User> user = userRepository.findById(userIdx);
        int point = user.get().getPoint();
        if (point < 20) {
            throw new AccountException(ErrorType.USER_POINT_LACK);
        }else {
            Lecture lecture = lectureService.findById(lectureId);

            ViewExam viewExam = new ViewExam();
            int count = user.get().getViewExamCount();

            user.get().setViewExamCount(count + 1);
            user.get().setPoint(point - 20);
            viewExam.setUserInViewExam(user.get());
            viewExam.setLectureInViewExam(lecture);
            jpaViewExamRepository.save(viewExam);
        }
    }

    public boolean verifyAuth(Long lectureId, Long userIdx){
        List<ViewExam> list = jpaViewExamRepository.findByUserId(userIdx);

        for (ViewExam viewExam : list) {
            if(viewExam.getLecture().getId().equals(lectureId)){
                return true;
            }
        }
        return false;
    }
}
