package Suwikibackend.Suwiki.domain;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.SuwikiApplication;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.dto.lecture.LectureFindOption;
import usw.suwiki.dto.lecture.LectureListAndCountDto;
import usw.suwiki.repository.evaluation.JpaEvaluatePostsRepository;
import usw.suwiki.repository.exam.JpaExamPostsRepository;
import usw.suwiki.repository.lecture.JpaLectureRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Transactional
@SpringBootTest(classes = SuwikiApplication.class)
public class Test1 {

    @Autowired
    JpaEvaluatePostsRepository jpaEvaluatePostsRepository;
    @Autowired
    JpaLectureRepository jpaLectureRepository;
    @Autowired
    JpaExamPostsRepository jpaExamPostsRepository;

    @Test
    public void findSubOrProf() {

        Optional<String> option = Optional.of("modifiedDate");
        Optional<Integer> page = Optional.of(1);

        LectureListAndCountDto dto = jpaLectureRepository.findAllLectureByFindOption(new LectureFindOption(option, page));
        System.out.println(dto.getLectureList());
        System.out.println(dto.getCount());
    }
}

