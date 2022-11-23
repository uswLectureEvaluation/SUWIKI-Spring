package usw.suwiki.domain.evaluation;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureService;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.global.PageOption;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class EvaluatePostsService {
    private final EvaluatePostsRepository evaluatePostsRepository;
    private final LectureService lectureService;
    private final UserRepository userRepository;

    public void save(EvaluatePostsSaveDto dto, Long userIdx, Long lectureId) {
        EvaluatePosts posts = new EvaluatePosts(dto);

        Lecture lecture = lectureService.findById(lectureId);
        Optional<User> user = userRepository.findById(userIdx);

        if (lecture == null) {
            new AccountException(ErrorType.NOT_EXISTS_LECTURE);
        } else {
            posts.setLecture(lecture);
            posts.setUser(user.get());  // user 도 넣어줘야 함
            Integer point = posts.getUser().getPoint();
            Integer num = posts.getUser().getWrittenEvaluation();
            posts.getUser().setPoint(point + 10);
            posts.getUser().setWrittenEvaluation(num + 1);
            EvaluatePostsToLecture newDto = new EvaluatePostsToLecture(posts);
            lectureService.addLectureValue(newDto);
            lectureService.calcLectureAvg(newDto);
            evaluatePostsRepository.save(posts);
        }
    }

    public EvaluatePosts findById(Long evaluateIdx) {
        return evaluatePostsRepository.findById(evaluateIdx);
    }

    public void update(Long evaluateIdx, EvaluatePostsUpdateDto dto) {
        EvaluatePosts posts = evaluatePostsRepository.findById(evaluateIdx);
        lectureService.cancelLectureValue(new EvaluatePostsToLecture(posts));
        posts.update(dto);
        EvaluatePostsToLecture newDto = new EvaluatePostsToLecture(posts);
        lectureService.addLectureValue(newDto);
        lectureService.calcLectureAvg(newDto);
    }

    public List<EvaluateResponseByLectureIdDto> findEvaluatePostsByLectureId(PageOption option, Long lectureId) {
        List<EvaluateResponseByLectureIdDto> dtoList = new ArrayList<>();
        List<EvaluatePosts> list = evaluatePostsRepository.findByLectureId(option, lectureId);
        for (EvaluatePosts post : list) {
            dtoList.add(new EvaluateResponseByLectureIdDto(post));
        }
        return dtoList;
    }

    public List<EvaluateResponseByUserIdxDto> findEvaluatePostsByUserId(PageOption option, Long userId) {
        List<EvaluateResponseByUserIdxDto> dtoList = new ArrayList<>();
        List<EvaluatePosts> list = evaluatePostsRepository.findByUserId(option, userId);
        for (EvaluatePosts post : list) {
            EvaluateResponseByUserIdxDto dto = new EvaluateResponseByUserIdxDto(post);
            dto.setSemesterList(post.getLecture().getSemesterList());
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean verifyWriteEvaluatePosts(Long userIdx, Long lectureId) {
        Lecture lecture = lectureService.findById(lectureId);
        Optional<User> user = userRepository.findById(userIdx);
        return evaluatePostsRepository.verifyPostsByIdx(user.get(), lecture);
    }

    public boolean verifyDeleteEvaluatePosts(Long userIdx, Long evaluateIdx) {
        EvaluatePosts posts = evaluatePostsRepository.findById(evaluateIdx);
        Integer point = posts.getUser().getPoint();
        if (point >= 30) {
            posts.getUser().setPoint(point - 30);
            return true;
        }
        return false;
    }

    public void deleteByUser(Long userIdx) {
        List<EvaluatePosts> list = evaluatePostsRepository.findAllByUserId(userIdx);

        if (list.isEmpty()) {
            return;
        } else {
            for (EvaluatePosts evaluatePosts : list) {
                EvaluatePostsToLecture dto = new EvaluatePostsToLecture(evaluatePosts);
                lectureService.cancelLectureValue(dto);
                lectureService.calcLectureAvg(dto);
                evaluatePostsRepository.delete(evaluatePosts);
            }
        }
    }

    @Synchronized
    public void deleteById(Long evaluateIdx, Long userIdx) {
        EvaluatePosts posts = evaluatePostsRepository.findById(evaluateIdx);
        Optional<User> user = userRepository.findById(userIdx);
        EvaluatePostsToLecture dto = new EvaluatePostsToLecture(posts);

        lectureService.cancelLectureValue(dto);
        lectureService.calcLectureAvg(dto);
        Integer postsCount = user.get().getWrittenEvaluation();
        user.get().setWrittenEvaluation(postsCount - 1);
        evaluatePostsRepository.delete(posts);
    }
}
