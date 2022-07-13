//package Suwikibackend.Suwiki.repository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import usw.suwiki.SuwikiApplication;
//import usw.suwiki.domain.evaluation.*;
//import usw.suwiki.domain.lecture.JpaLectureRepository;
//import usw.suwiki.domain.lecture.Lecture;
//import usw.suwiki.domain.lecture.LectureService;
//import usw.suwiki.domain.user.UserService;
//import usw.suwiki.global.PageOption;
//
//import javax.transaction.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//
//@Transactional
//@SpringBootTest(classes = SuwikiApplication.class)
//public class EvaluatePostsTest {
//
//    @Autowired
//    EvaluatePostsService evaluatePostsService;
//
//    @Autowired
//    JpaEvaluatePostsRepository evaluatePostsRepository;
//
//    @Autowired
//    JpaLectureRepository lectureRepository;
//
//    @Autowired
//    LectureService lectureService;
//
//    @Autowired
//    UserService userService;
//
//    @BeforeEach
//    void setting_evaluate() {
//        evaluatePostsService.save(EvaluatePostsSaveDto.builder()
//                .lectureName("조직리더십")
//                .content("test")
//                .team(2)
//                .learning(5.1f).homework(5).difficulty(5)
//                .honey(5.0f)
//                .satisfaction(5.0f)
//                .build(), 1L, 12L);
//    }
//
//    @Test
//    void before_delete_lectureTotal() {
//        Lecture lecture = lectureRepository.findById(12L);
//        System.out.println(lecture.getLectureTotalAvg() + ", " + lecture.getLectureHomeworkAvg() + " , " + lecture.getLectureDifficultyAvg() + ", ");
//        assertThat(lecture.getLectureDifficultyValue()).isEqualTo(5);
//    }
//
////    @Test
////    void after_delete_user_lectureTotal() {
////        Lecture lecture = lectureRepository.findById(12L);
////        evaluatePostsService.deleteByUser(1L);
////        System.out.println(lecture.getLectureDifficultyValue() + ", " + lecture.getLectureHoneyValue() + " , " + lecture.getLectureTeamValue() + ", ");
////        assertThat(lecture.getLectureDifficultyValue()).isEqualTo(0);
////    }
//
//    @Test
//    void DeleteByUserTest() {
//
//        Long userIdx = 1L;
//        Lecture lecture = lectureRepository.findById(12L);
//        List<EvaluatePosts> list = evaluatePostsRepository.findAllByUserId(userIdx);
//        if (list.isEmpty()) {
//            return;
//        } else {
//            for (EvaluatePosts evaluatePosts : list) {
//                EvaluatePostsToLecture dto = new EvaluatePostsToLecture(evaluatePosts);
//                lectureService.cancelLectureValue(dto);
//                lectureService.calcLectureAvg(dto);
//                evaluatePostsRepository.delete(evaluatePosts);
//            }
//        }
//
//        assertThat(lecture.getLectureDifficultyValue()).isEqualTo(-4.4);
//    }
//
//}