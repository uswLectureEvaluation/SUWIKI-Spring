package usw.suwiki.domain.exampost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.core.exception.ExamPostException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.exampost.ExamPost;
import usw.suwiki.domain.exampost.ExamPostRepository;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExamPostCRUDService {
    private static final int PAGE_LIMIT = 10;

    private final ExamPostRepository examPostRepository;
    private final UserRepository userRepository;

    public List<ExamPost> loadExamPostListFromUserIdx(Long userIdx) {
        return examPostRepository.findAllByUser(userRepository.findById(userIdx).get());
    }

    public ExamPost loadExamPostFromExamPostIdx(Long examIdx) {
        return examPostRepository.findById(examIdx)
          .orElseThrow(() -> new ExamPostException(ExceptionType.EXAM_POST_NOT_FOUND));
    }

    @Transactional
    public void save(ExamPost examPost) {
        examPostRepository.save(examPost);
    }

    @Transactional
    public void deleteFromUserIdx(Long userIdx) {
        List<ExamPost> examPosts = loadExamPostListFromUserIdx(userIdx);
        examPostRepository.deleteAll(examPosts);
    }

    public List<ExamPost> loadExamPostsFromLectureIdx(Long lectureIdx, PageOption option) {
        return examPostRepository.findAllByLectureId(lectureIdx, option.getOffset(), PAGE_LIMIT);
    }

    public List<ExamPost> loadExamPostsFromUserIdxAndPageOption(Long userIdx, PageOption option) {
        return examPostRepository.findByUserIdxAndPagePotion(userIdx, option.getOffset(), PAGE_LIMIT);
    }

    public boolean isWrite(User user, Lecture lecture) {
        return examPostRepository.findByUserAndLecture(user, lecture).isPresent();
    }

    @Transactional
    public void delete(ExamPost examPost) {
        examPostRepository.delete(examPost);
    }
}
