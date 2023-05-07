package usw.suwiki.domain.exam.service;

import com.mysql.cj.MysqlConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.exam.controller.dto.*;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.domain.repository.ExamPostsRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.service.UserService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ExamPostsService {
    private final ExamPostsRepository examPostsRepository;
    private final LectureService lectureService;
    private final UserService userService;

    public void write(ExamPostsSaveDto dto, Long userIdx, Long lectureId) {
        ExamPosts posts = new ExamPosts(dto);
        Lecture lecture = lectureService.findById(lectureId);
        User user = userService.loadUserFromUserIdx(userIdx);

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
        return examPostsRepository.isWrite(
                userService.loadUserFromUserIdx(userIdx),
                lectureService.findById(lectureId)
        );
    }

    public void deleteFromUserIdx(Long userIdx) {
        List<ExamPosts> list = examPostsRepository.findAllByUserId(userIdx);

        if (!list.isEmpty()) {
            for (ExamPosts examPosts : list) {
                examPostsRepository.delete(examPosts);
            }
        }
    }

    public void executeDeleteExamPosts(Long userIdx, Long examIdx) {
        ExamPosts post = examPostsRepository.findById(examIdx);
        User user = userService.loadUserFromUserIdx(userIdx);
        user.decreasePointAndWrittenExamByDeleteExamPosts();
        examPostsRepository.delete(post);
    }

    public ExamPosts loadExamPostsFromExamPostsIdx(Long examIdx) {
        return examPostsRepository.findById(examIdx);
    }
}
