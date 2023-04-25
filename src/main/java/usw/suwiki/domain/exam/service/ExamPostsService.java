package usw.suwiki.domain.exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.exam.dto.ExamPostsSaveDto;
import usw.suwiki.domain.exam.dto.ExamPostsUpdateDto;
import usw.suwiki.domain.exam.dto.ExamResponseByLectureIdDto;
import usw.suwiki.domain.exam.dto.ExamResponseByUserIdxDto;
import usw.suwiki.domain.exam.entity.ExamPosts;
import usw.suwiki.domain.exam.repository.ExamPostsRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExamPostsService {
    private final ExamPostsRepository examPostsRepository;
    private final LectureService lectureService;
    private final UserRepository userRepository;

    public void save(ExamPostsSaveDto dto, Long userIdx, Long lectureId) {
        ExamPosts posts = new ExamPosts(dto);
        Lecture lecture = lectureService.findById(lectureId);
        Optional<User> user = userRepository.findById(userIdx);

        if (lecture == null) {
            throw new AccountException(ExceptionType.NOT_EXISTS_LECTURE);
        } else {
            posts.setLecture(lecture);
            posts.setUser(user.get());
            userRepository.updatePoint(userIdx, (user.get().getPoint() + 20));
            userRepository.updateWrittenExamCount(userIdx, user.get().getWrittenExam() + 1);
            examPostsRepository.save(posts);
        }
    }

    public ExamPosts findById(Long examIdx) {
        return examPostsRepository.findById(examIdx);
    }

    public void update(Long examIdx, ExamPostsUpdateDto dto) {
        ExamPosts posts = examPostsRepository.findById(examIdx);
        posts.update(dto);
    }

    public List<ExamResponseByLectureIdDto> findExamPostsByLectureId(PageOption option, Long lectureId) {
        List<ExamResponseByLectureIdDto> dtoList = new ArrayList<>();
        List<ExamPosts> list = examPostsRepository.findByLectureId(option, lectureId);
        for (ExamPosts post : list) {
            dtoList.add(new ExamResponseByLectureIdDto(post));
        }
        return dtoList;
    }

    public List<ExamResponseByUserIdxDto> findExamPostsByUserId(PageOption option, Long userId) {
        List<ExamResponseByUserIdxDto> dtoList = new ArrayList<>();
        List<ExamPosts> list = examPostsRepository.findByUserId(option, userId);
        for (ExamPosts post : list) {
            ExamResponseByUserIdxDto dto = new ExamResponseByUserIdxDto(post);
            dto.setSemesterList(post.getLecture().getSemester());
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean verifyWriteExamPosts(Long userIdx, Long lectureId) {
        Lecture lecture = lectureService.findById(lectureId);
        Optional<User> user = userRepository.findById(userIdx);
        return examPostsRepository.verifyPostsByIdx(user.get(), lecture);
    }

    public void deleteByUser(Long userIdx) {
        List<ExamPosts> list = examPostsRepository.findAllByUserId(userIdx);

        if (list.isEmpty()) {
            return;
        } else {
            for (ExamPosts examPosts : list) {
                examPostsRepository.delete(examPosts);
            }
        }
    }

    public boolean verifyDeleteExamPosts(Long userIdx, Long examIdx) {
        ExamPosts posts = examPostsRepository.findById(examIdx);
        Integer point = posts.getUser().getPoint();
        if (point >= 30) {
            userRepository.updatePoint(userIdx, (posts.getUser().getPoint() - 30));
            return true;
        }
        return false;
    }

    public void deleteById(Long examIdx, Long userIdx) {
        ExamPosts posts = examPostsRepository.findById(examIdx);
        userRepository.findById(userIdx)
                .orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_EXISTS));
        userRepository.updateWrittenExamCount(userIdx, posts.getUser().getPoint() - 30);
        examPostsRepository.delete(posts);
    }
}
