package usw.suwiki.domain.exampost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.exampost.controller.dto.ExamPostUpdateDto;
import usw.suwiki.domain.exampost.controller.dto.ExamPostsSaveDto;
import usw.suwiki.domain.exampost.controller.dto.ExamResponseByLectureIdDto;
import usw.suwiki.domain.exampost.controller.dto.ExamResponseByUserIdxDto;
import usw.suwiki.domain.exampost.controller.dto.ReadExamPostResponse;
import usw.suwiki.domain.exampost.controller.dto.viewexam.PurchaseHistoryDto;
import usw.suwiki.domain.exampost.domain.ExamPost;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.domain.userlecture.viewexam.ViewExam;
import usw.suwiki.domain.userlecture.viewexam.service.ViewExamCRUDService;
import usw.suwiki.global.PageOption;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamPostService {

    private final ExamPostCRUDService examPostCRUDService;
    private final ViewExamCRUDService viewExamCRUDService;
    private final LectureService lectureService;
    private final UserCRUDService userCRUDService;

    @Transactional
    public void write(ExamPostsSaveDto examData, Long userIdx, Long lectureId) {
        Lecture lecture = lectureService.findLectureById(lectureId);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);

        ExamPost examPost = createExamPost(examData, user, lecture);
        user.increasePointByWritingExamPost();

        examPostCRUDService.save(examPost);
    }

    @Transactional
    public void purchase(Long lectureIdx, Long userIdx) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        Lecture lecture =lectureService.findLectureById(lectureIdx);
        user.purchaseExamPost();

        ViewExam viewExam = ViewExam.builder()
            .user(user)
            .lecture(lecture)
            .build();
        viewExamCRUDService.save(viewExam);
    }

    @Transactional(readOnly = true)
    public boolean canRead(Long userId, Long lectureId) {
        return viewExamCRUDService.isExist(userId, lectureId);
    }

    @Transactional(readOnly = true)
    public List<PurchaseHistoryDto> readPurchaseHistory(Long userIdx) {
        List<PurchaseHistoryDto> response = new ArrayList<>();
        List<ViewExam> viewExams = viewExamCRUDService.loadViewExamsFromUserIdx(userIdx);
        for (ViewExam viewExam : viewExams) {
            PurchaseHistoryDto data = PurchaseHistoryDto.builder()
                .id(viewExam.getId())
                .lectureName(viewExam.getLecture().getName())
                .professor(viewExam.getLecture().getProfessor())
                .majorType(viewExam.getLecture().getMajorType())
                .createDate(viewExam.getCreateDate())
                .build();
            response.add(data);
        }
        return response;
    }

    @Transactional
    public void update(Long examIdx, ExamPostUpdateDto examUpdateData) {
        ExamPost examPost = examPostCRUDService.loadExamPostFromExamPostIdx(examIdx);
        examPost.update(examUpdateData);
    }

    @Transactional(readOnly = true)
    public ReadExamPostResponse readExamPost(Long userId, Long lectureId, PageOption option) {
        List<ExamResponseByLectureIdDto> response = new ArrayList<>();
        List<ExamPost> examPosts = examPostCRUDService.loadExamPostsFromLectureIdx(lectureId, option);
        boolean isWrite = isWrite(userId, lectureId);
        for (ExamPost post : examPosts) {
            response.add(new ExamResponseByLectureIdDto(post));
        }
        if (response.isEmpty()) {
            return ReadExamPostResponse.hasNotExamPost(isWrite);
        }
        return ReadExamPostResponse.hasExamPost(response, isWrite);
    }


    @Transactional(readOnly = true)
    public List<ExamResponseByUserIdxDto> readExamPostByUserIdAndOption(PageOption option, Long userId) {
        List<ExamResponseByUserIdxDto> response = new ArrayList<>();
        List<ExamPost> examPosts = examPostCRUDService.loadExamPostsFromUserIdxAndPageOption(userId, option);
        for (ExamPost examPost : examPosts) {
            ExamResponseByUserIdxDto data = new ExamResponseByUserIdxDto(examPost);
            data.setSemesterList(examPost.getLecture().getSemester());
            response.add(data);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public boolean isWrite(Long userIdx, Long lectureIdx) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        Lecture lecture = lectureService.findLectureById(lectureIdx);
        return examPostCRUDService.isWrite(user, lecture);
    }

    @Transactional
    public void executeDeleteExamPosts(Long userIdx, Long examIdx) {
        ExamPost post = examPostCRUDService.loadExamPostFromExamPostIdx(examIdx);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.decreasePointAndWrittenExamByDeleteExamPosts();
        examPostCRUDService.delete(post);
    }

    private ExamPost createExamPost(ExamPostsSaveDto examData, User user, Lecture lecture) {
        ExamPost examPost = new ExamPost(examData);
        examPost.setLecture(lecture);
        examPost.setUser(user);

        return examPost;
    }
}
