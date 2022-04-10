//package Suwikibackend.Suwiki.domain;
//
//import usw.suwiki.SuwikiApplication;
//import usw.suwiki.repository.evaluation.JpaEvaluatePostsRepository;
//import usw.suwiki.repository.exam.JpaExamPostsRepository;
//import usw.suwiki.repository.lecture.JpaLectureRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import javax.transaction.Transactional;
//import java.util.List;
//
//@Transactional
//@SpringBootTest(classes = SuwikiApplication.class)
//public class JpaRepositoryTest {
//
//    @Autowired
//    JpaEvaluatePostsRepository jpaEvaluatePostsRepository;
//    @Autowired
//    JpaLectureRepository jpaLectureRepository;
//    @Autowired
//    JpaExamPostsRepository jpaExamPostsRepository;
//
//    @Test
//    public void findSubOrProf() {
//        List<Object[]> list = jpaLectureRepository.findAllLectureByFindOption2();
//        for (Object[] objects : list) {
//            for (Object object : objects) {
//                System.out.println(object.toString());
//            }
//        }
//    }
//}
//
////    @Test
////    public void findByProfNameTest(){
////        //List<Lecture> resultList = jpaLectureRepository.findByProfessorName("최");
////        List<Lecture> resultList = jpaLectureRepository.findLectureByProfNmFindOption("김영환",new LectureFindOption());
////        System.out.println("11111111111111111");
////        System.out.println("resultList = " + resultList);
////        System.out.println("11111111111111111");
////
////    }
////
//////    @Test
//////    public void findByLectureId(){
//////        //List<Lecture> resultList = jpaLectureRepository.findByProfessorName("최");
//////        List<EvaluatePosts> resultList = jpaEvaluatePostsRepository.findByLectureId(1L);
//////        System.out.println("11111111111111111");
//////        System.out.println("resultList = " + resultList);
//////        System.out.println("11111111111111111");
//////
//////    }
////
////    @Test
////    public void findOrderBySatisfactionTest(){
////        //List<Lecture> resultList = jpaLectureRepository.findByProfessorName("최");
////        List<Lecture> resultList = jpaLectureRepository.findAllLectureByFindOption(new LectureFindOption());
////        List<Lecture> resultList2 = jpaLectureRepository.findLectureBySubjNmFindOption("도전과창조",new LectureFindOption());
////        System.out.println("11111111111111111");
////        System.out.println("resultList = " + resultList);
////        for (int i=0;i<resultList.size();i++) {
////            System.out.println("lecture.getLectureName() = " +"......" +resultList.get(i).getId());
////        }
////        System.out.println("11111111111111111");
////
////    }
//
//
//}
