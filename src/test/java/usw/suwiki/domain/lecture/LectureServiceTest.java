//package usw.suwiki.domain.lecture;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.transaction.Transactional;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class LectureServiceTest {
//
//    @Autowired
//    LectureRepository lectureRepository;
//
//    @Test
//    void findAllLectureByFindOption() {
//        LectureFindOption findOption = new LectureFindOption(Optional.of("modifiedDate"), Optional.of(1), Optional.empty());
//        LectureListAndCountDto lectures = lectureRepository.findAllLectureByFindOption(findOption);
//
//        for (Lecture lecture : lectures.getLectureList()) {
//            System.out.println(lecture.getPostsCount());
//        }
//
//    }
//}