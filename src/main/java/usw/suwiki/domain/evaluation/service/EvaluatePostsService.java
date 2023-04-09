package usw.suwiki.domain.evaluation.service;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;
import usw.suwiki.domain.evaluation.dto.EvaluatePostsSaveDto;
import usw.suwiki.domain.evaluation.dto.EvaluatePostsUpdateDto;
import usw.suwiki.domain.evaluation.dto.EvaluateResponseByLectureIdDto;
import usw.suwiki.domain.evaluation.dto.EvaluateResponseByUserIdxDto;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.repository.EvaluatePostsRepository;
import usw.suwiki.domain.lecture.entity.Lecture;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.ErrorType;
import usw.suwiki.global.exception.errortype.AccountException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class EvaluatePostsService {
    private final EvaluatePostsRepository evaluatePostsRepository;
    private final LectureService lectureService;
    private final UserRepository userRepository;

    public void save(EvaluatePostsSaveDto evaluatePostsSaveDto, Long userIdx, Long lectureId) {
        EvaluatePosts posts = new EvaluatePosts(evaluatePostsSaveDto);
        Lecture lecture = lectureService.findById(lectureId);
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new AccountException(ErrorType.USER_NOT_EXISTS));
        if (lecture == null) {
            throw new AccountException(ErrorType.NOT_EXISTS_LECTURE);
        }
        posts.setLecture(lecture);
        posts.setUser(user);
        userRepository.updatePoint(userIdx, (user.getPoint() + 10));
        userRepository.updateWrittenEvaluateCount(userIdx, user.getWrittenEvaluation() + 1);
        EvaluatePostsToLecture lectureEvaluation = new EvaluatePostsToLecture(posts);
        lectureService.updateLectureEvaluationIfCreateNewPost(lectureEvaluation);

        evaluatePostsRepository.save(posts);
    }

    public EvaluatePosts findById(Long evaluateIdx) {
        return evaluatePostsRepository.findById(evaluateIdx);
    }

    public void update(Long evaluateIdx, EvaluatePostsUpdateDto dto) {
        EvaluatePosts post = evaluatePostsRepository.findById(evaluateIdx);
        EvaluatePostsToLecture beforeUpdated = new EvaluatePostsToLecture(post);
        post.update(dto);
        EvaluatePostsToLecture updated = new EvaluatePostsToLecture(post);
        lectureService.updateLectureEvaluationIfUpdatePost(beforeUpdated, updated);
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

    public boolean verifyIsUserWriteEvaluatePost(Long userIdx, Long lectureId) {
        Lecture lecture = lectureService.findById(lectureId);
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new AccountException(ErrorType.USER_NOT_EXISTS));
        ;
        return evaluatePostsRepository.verifyPostsByIdx(user, lecture);
    }

    public boolean deleteEvaluatePost(Long userIdx, Long evaluateIdx) {
        EvaluatePosts posts = evaluatePostsRepository.findById(evaluateIdx);
        Integer point = posts.getUser().getPoint();
        if (point >= 30) {
            userRepository.updatePoint(userIdx, (posts.getUser().getPoint() - 30));
            return true;
        }
        return false;
    }

    public void deleteByUser(Long userIdx) {
        List<EvaluatePosts> list = evaluatePostsRepository.findAllByUserId(userIdx);
        if (!list.isEmpty()) {
            for (EvaluatePosts evaluatePosts : list) {
                EvaluatePostsToLecture lectureEvaluation = new EvaluatePostsToLecture(evaluatePosts);
                lectureService.updateLectureEvaluationIfDeletePost(lectureEvaluation);

                evaluatePostsRepository.delete(evaluatePosts);
            }
        }
    }

    @Synchronized
    public void deleteById(Long evaluateIdx, Long userIdx) {
        EvaluatePosts posts = evaluatePostsRepository.findById(evaluateIdx);
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new AccountException(ErrorType.USER_NOT_EXISTS));

        EvaluatePostsToLecture lectureEvaluation = new EvaluatePostsToLecture(posts);
        lectureService.updateLectureEvaluationIfDeletePost(lectureEvaluation);

        userRepository.updateWrittenEvaluateCount(userIdx, user.getWrittenEvaluation() - 1);
        evaluatePostsRepository.delete(posts);
    }
}
