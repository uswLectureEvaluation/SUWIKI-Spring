package Suwikibackend.Suwiki.service;

import lombok.Synchronized;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.SuwikiApplication;
import usw.suwiki.domain.evaluation.EvaluatePostsSaveDto;
import usw.suwiki.domain.evaluation.EvaluatePostsService;
import usw.suwiki.domain.evaluation.JpaEvaluatePostsRepository;
import usw.suwiki.domain.exam.ExamPostsSaveDto;
import usw.suwiki.domain.exam.ExamPostsService;
import usw.suwiki.domain.lecture.JpaLectureRepository;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.Role;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.UserService;
import usw.suwiki.global.PageOption;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest(classes = SuwikiApplication.class)
public class evaluatePostsTest {

    @Autowired
    EvaluatePostsService evaluatePostsService;

    @Autowired
    ExamPostsService examPostsService;

    @Autowired
    JpaEvaluatePostsRepository evaluatePostsRepository;

    @Autowired
    JpaLectureRepository lectureRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setting_evaluate() {
        evaluatePostsService.save(EvaluatePostsSaveDto.builder()
                        .lectureName("도전과창조-기업가정신")
                        .content("test")
                        .team(2)
                .learning(5.1f).homework(5).difficulty(5)
                .honey(5.0f)
                .satisfaction(5.0f)
                .build(), 22L, 1L);

        examPostsService.save(new ExamPostsSaveDto(
                "도전과창조-기업가정신", "2021-2", "김영환", "test", "test", "test", "test"
        ), 22L, 1L);
    }

    @Test
    void write_evaluation_point() {
        Optional<User> user = userRepository.findById(22L);
        assertThat(user.get().getPoint()).isEqualTo(30);
    }

    @Test
    void write_exam_point() {
        Optional<User> user = userRepository.findById(22L);
        assertThat(user.get().getPoint()).isEqualTo(30);
    }

    @Test
    void before_delete_lectureTotal() {
        Lecture lecture = lectureRepository.findById(1L);
        System.out.println(lecture.getLectureTotalAvg() + ", " + lecture.getLectureHomeworkAvg() + " , " + lecture.getLectureDifficultyAvg() + ", ");
        assertThat(lecture.getLectureDifficultyValue()).isEqualTo(5);
    }

    @Test
    void after_delete_user_lectureTotal() {
        Lecture lecture = lectureRepository.findById(1L);
        evaluatePostsService.deleteByUser(4L);
        System.out.println(lecture.getLectureDifficultyValue() + ", " + lecture.getLectureHoneyValue() + " , " + lecture.getLectureTeamValue() + ", ");
        assertThat(lecture.getLectureDifficultyValue()).isEqualTo(0);
    }

    @Test
    void after_delete_user_lectureTotal2() {
        Lecture lecture = lectureRepository.findById(1L);
        evaluatePostsService.deleteById(1L,1L);
        System.out.println(lecture.getLectureDifficultyValue() + ", " + lecture.getLectureHoneyValue() + " , " + lecture.getLectureTeamValue() + ", ");
        assertThat(lecture.getLectureDifficultyValue()).isEqualTo(0);
    }

}

