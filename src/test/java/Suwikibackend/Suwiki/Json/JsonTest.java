//package Suwikibackend.Suwiki.Json;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.ParseException;
//import org.springframework.transaction.annotation.Transactional;
//import usw.suwiki.SuwikiApplication;
//import usw.suwiki.domain.lecture.Lecture;
//import usw.suwiki.domain.evaluation.JpaEvaluatePostsRepository;
//import usw.suwiki.service.util.JsonToDataTable;
//import usw.suwiki.domain.lecture.JpaLectureRepository;
//import org.json.simple.parser.ParseException;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import usw.suwiki.service.util.JsonToDataTable;
//
//import java.io.IOException;
//import java.util.HashMap;
//
////@Transactional
//@SpringBootTest(classes = SuwikiApplication.class)
//public class JsonTest {
//
//    @Autowired
//    JsonToDataTable jsonToDataTable;
//
//    @Test
//    public void registration_json() throws IOException, ParseException, InterruptedException {
//        jsonToDataTable.toEntity();
//    }
//
////    @Test
////    public void middotTest() throws IOException, ParseException, InterruptedException {
////        HashMap<String, String> hashMap = new HashMap<>();
////        hashMap.put("majorType", "화학공학·신소재공학부");
////        JSONObject jsonObject1 = new JSONObject(hashMap);
////
////        String majorType = String.valueOf(jsonObject1.get("majorType"));
////        if(majorType.contains("·")){
////            majorType = majorType.replace("·", "-");
////        }
////        System.out.println(majorType);
////
////    }
//}
