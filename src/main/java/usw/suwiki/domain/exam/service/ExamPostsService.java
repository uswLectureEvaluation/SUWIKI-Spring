package usw.suwiki.domain.exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import usw.suwiki.domain.exam.controller.dto.ReadExamPostResponse;
import usw.suwiki.domain.exam.controller.dto.ExamPostsSaveDto;
import usw.suwiki.domain.exam.controller.dto.ExamPostsUpdateDto;
import usw.suwiki.domain.exam.controller.dto.ExamResponseByLectureIdDto;
import usw.suwiki.domain.exam.controller.dto.ExamResponseByUserIdxDto;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.domain.repository.ExamPostsRepository;
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

    public void write(ExamPostsSaveDto dto, Long userIdx, Long lectureId) {
        ExamPosts posts = new ExamPosts(dto);
        Lecture lecture = lectureService.findById(lectureId);
        User user = userRepository.findById(userIdx).get();

        if (lecture == null) {
            throw new AccountException(ExceptionType.NOT_EXISTS_LECTURE);
        }
        posts.setLecture(lecture);
        posts.setUser(user);
        user.increasePointByWritingExamPost();
        examPostsRepository.save(posts);
    }

    public ExamPosts findById(Long examIdx) {
        return examPostsRepository.findById(examIdx);
    }

    public void update(Long examIdx, ExamPostsUpdateDto dto) {
        ExamPosts posts = examPostsRepository.findById(examIdx);
        posts.update(dto);
    }

    public ReadExamPostResponse readExamPost(Long userId, Long lectureId, PageOption option) {
        List<ExamResponseByLectureIdDto> result = new ArrayList<>();
        List<ExamPosts> list = examPostsRepository.findByLectureId(option, lectureId);
        boolean isWrite = isWrite(userId, lectureId);
        for (ExamPosts post : list) {
            result.add(new ExamResponseByLectureIdDto(post));
        }

        if (result.isEmpty()) {
            return ReadExamPostResponse.hasNotExamPost(isWrite);
        }
        return ReadExamPostResponse.hasExamPost(result, isWrite);
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

    public boolean isWrite(Long userIdx, Long lectureId) {
        Lecture lecture = lectureService.findById(lectureId);
        Optional<User> user = userRepository.findById(userIdx);
        return examPostsRepository.isWrite(user.get(), lecture);
    }

    public void deleteByUser(Long userIdx) {
        List<ExamPosts> list = examPostsRepository.findAllByUserId(userIdx);

        if (!list.isEmpty()) {
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
