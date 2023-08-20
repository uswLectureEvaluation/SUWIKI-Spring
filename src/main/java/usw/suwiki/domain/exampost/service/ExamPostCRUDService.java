package usw.suwiki.domain.exampost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.exampost.domain.ExamPost;
import usw.suwiki.domain.exampost.domain.repository.ExamPostRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.ExamPostException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamPostCRUDService {

    private static final int LIMIT_PAGE_SIZE = 10;

    private final ExamPostRepository examPostRepository;
    private final UserCRUDService userCRUDService;

    public void save(ExamPost examPost) {
        examPostRepository.save(examPost);
    }

    public ExamPost loadExamPostFromExamPostIdx(Long examIdx) {
        Optional<ExamPost> examPost = examPostRepository.findById(examIdx);
        if (examPost.isPresent()) {
            return examPost.get();
        }
        throw new ExamPostException(ExceptionType.EXAM_POST_NOT_FOUND);
    }

    public void deleteFromUserIdx(Long userIdx) {
        examPostRepository.deleteAll(loadExamPostListFromUserIdx(userIdx));
    }

    public List<ExamPost> loadExamPostsFromLectureIdx(Long lectureIdx, PageOption option) {
        return examPostRepository.findAllByLectureId(
                lectureIdx,
                option.getPageNumber().get(),
                LIMIT_PAGE_SIZE
        );
    }

    public List<ExamPost> loadExamPostsFromUserIdxAndPageOption(Long userIdx, PageOption option) {
        return examPostRepository.findByUserIdxAndPagePotion(
                userIdx,
                option.getPageNumber().get(),
                LIMIT_PAGE_SIZE
        );
    }

    public boolean isWrite(User user, Lecture lecture) {
        return examPostRepository.findByUserAndLecture(user, lecture).isPresent();
    }

    public List<ExamPost> loadExamPostListFromUserIdx(Long userIdx) {
        return examPostRepository.findAllByUser(userCRUDService.loadUserFromUserIdx(userIdx));
    }

    public void delete(ExamPost examPost) {
        examPostRepository.delete(examPost);
    }
}
