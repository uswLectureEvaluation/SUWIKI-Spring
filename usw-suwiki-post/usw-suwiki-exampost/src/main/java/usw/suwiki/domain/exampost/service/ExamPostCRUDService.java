package usw.suwiki.domain.exampost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.errortype.ExamPostException;
import usw.suwiki.domain.exampost.ExamPost;
import usw.suwiki.domain.exampost.ExamPostRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.ExceptionType;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ExamPostCRUDService {

    private static final int LIMIT_PAGE_SIZE = 10;

    private final ExamPostRepository examPostRepository;
    private final UserRepository userRepository;

    public List<ExamPost> loadExamPostListFromUserIdx(Long userIdx) {
        return examPostRepository.findAllByUser(userRepository.findById(userIdx).get());
    }

    public ExamPost loadExamPostFromExamPostIdx(Long examIdx) {
        Optional<ExamPost> examPost = examPostRepository.findById(examIdx);
        if (examPost.isPresent()) {
            return examPost.get();
        }
        throw new ExamPostException(ExceptionType.EXAM_POST_NOT_FOUND);
    }

    public void save(ExamPost examPost) {
        examPostRepository.save(examPost);
    }

    public void deleteFromUserIdx(Long userIdx) {
        examPostRepository.deleteAll(loadExamPostListFromUserIdx(userIdx));
    }

    public List<ExamPost> loadExamPostsFromLectureIdx(Long lectureIdx, PageOption option) {
        return examPostRepository.findAllByLectureId(
                lectureIdx,
                option.getOffset(),
                LIMIT_PAGE_SIZE
        );
    }

    public List<ExamPost> loadExamPostsFromUserIdxAndPageOption(Long userIdx, PageOption option) {
        return examPostRepository.findByUserIdxAndPagePotion(
                userIdx,
                option.getOffset(),
                LIMIT_PAGE_SIZE
        );
    }

    public boolean isWrite(User user, Lecture lecture) {
        return examPostRepository.findByUserAndLecture(user, lecture).isPresent();
    }

    public void delete(ExamPost examPost) {
        examPostRepository.delete(examPost);
    }
}
