package usw.suwiki.service.exam;

import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.user.User;
import usw.suwiki.dto.evaluate.EvaluatePostsUpdateDto;
import usw.suwiki.dto.exam_info.ExamPostsUpdateDto;
import usw.suwiki.dto.exam_info.ExamResponseByUserIdxDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.repository.user.UserRepository;
import usw.suwiki.service.lecture.LectureService;
import usw.suwiki.dto.PageOption;
import usw.suwiki.dto.exam_info.ExamPostsSaveDto;
import usw.suwiki.dto.exam_info.ExamResponseByLectureIdDto;
import usw.suwiki.domain.exam.ExamPosts;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.repository.exam.ExamPostsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void save(ExamPostsSaveDto dto, Long userIdx, Long lectureId){
        ExamPosts posts = new ExamPosts(dto);
        Lecture lecture = lectureService.findById(lectureId);
        Optional<User> user = userRepository.findById(userIdx);

        if(lecture == null){
            throw new AccountException(ErrorType.NOT_EXISTS_LECTURE);
        }else {
            posts.setLecture(lecture);
            posts.setUser(user.get());
            Integer point = posts.getUser().getPoint();
            Integer num = posts.getUser().getWrittenExam();
            posts.getUser().setPoint(point + 20);
            posts.getUser().setWrittenExam(num + 1);
            examPostsRepository.save(posts);
        }
    }

    public ExamPosts findById(Long examIdx){
        return examPostsRepository.findById(examIdx);
    }

    public void update(Long examIdx , ExamPostsUpdateDto dto){
        ExamPosts posts = examPostsRepository.findById(examIdx);
        posts.update(dto);
    }

    public List<ExamResponseByLectureIdDto> findExamPostsByLectureId(PageOption option , Long lectureId){
        List<ExamResponseByLectureIdDto> dtoList = new ArrayList<>();
        List<ExamPosts> list = examPostsRepository.findByLectureId(option, lectureId);
        for (ExamPosts post : list) {
            dtoList.add(new ExamResponseByLectureIdDto(post));
        }
        return dtoList;
    }

    public List<ExamResponseByUserIdxDto> findExamPostsByUserId(PageOption option , Long userId){
        List<ExamResponseByUserIdxDto> dtoList = new ArrayList<>();
        List<ExamPosts> list = examPostsRepository.findByUserId(option, userId);
        for (ExamPosts post : list) {
            ExamResponseByUserIdxDto dto = new ExamResponseByUserIdxDto(post);
            dto.setSemesterList(post.getLecture().getSemester());
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean verifyWriteExamPosts(Long userIdx, Long lectureId){
        Lecture lecture = lectureService.findById(lectureId);
        Optional<User> user = userRepository.findById(userIdx);
        return examPostsRepository.verifyPostsByIdx(user.get(), lecture);
    }

    public void deleteByUser(Long userIdx){
        List<ExamPosts> list = examPostsRepository.findAllByUserId(userIdx);

        if (list.isEmpty()) {
            return;
        } else {
            for (ExamPosts examPosts : list) {
                examPostsRepository.delete(examPosts);
            }
        }
    }

    public boolean verifyDeleteExamPosts(Long userIdx, Long examIdx){
        ExamPosts posts = examPostsRepository.findById(examIdx);
        Integer point = posts.getUser().getPoint();
        if(point >= 20){
            posts.getUser().setPoint(point - 20);
            return true;
        }
        return false;
    }

    public void deleteById(Long examIdx, Long userIdx){
        ExamPosts posts = examPostsRepository.findById(examIdx);
        Optional<User> user = userRepository.findById(userIdx);
        Integer postsCount = user.get().getWrittenExam();
        user.get().setWrittenExam(postsCount - 1);
        examPostsRepository.delete(posts);
    }
}
