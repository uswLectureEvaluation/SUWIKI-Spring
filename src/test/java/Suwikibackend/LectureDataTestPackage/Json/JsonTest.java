//package Suwikibackend.LectureDataTestPackage.Json;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.ParseException;
//import usw.suwiki.SuwikiApplication;
//import usw.suwiki.global.util.JsonToDataTable;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
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
//    public synchronized void registration_json_2021() throws IOException, ParseException, InterruptedException {
////        String path21_02 = "/Users/BestFriend/Desktop/suwiki-remaster/src/main/resources/USW_2021_2 thirteen.json";
//         String path21_02 = "E:/Priority/Project/SUWIKI-REMASTER/src/main/resources/USW_2021_2 thirteen.json";
//        jsonToDataTable.toEntity(path21_02);
//    }
//
//    @Test
//    public synchronized void registration_json_2022() throws IOException, ParseException, InterruptedException {
////        String path22_01 = "/Users/BestFriend/Desktop/suwiki-remaster/src/main/resources/USW_2022_1 thirteen.json";
//         String path22_01 = "E:/Priority/Project/SUWIKI-REMASTER/src/main/resources/USW_2022_1 thirteen.json";
//        jsonToDataTable.toEntity(path22_01);
//    }
//
//    @Test
//    public void middotTest() throws IOException, ParseException, InterruptedException {
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("majorType", "화학공학·신소재공학부");
//        JSONObject jsonObject1 = new JSONObject(hashMap);
//
//        String majorType = String.valueOf(jsonObject1.get("majorType"));
//        if(majorType.contains("·")){
//            majorType = majorType.replace("·", "-");
//        }
//        System.out.println(majorType);
//
//    }
//
//        @Test
//        public void handleJsonData() throws IOException, ParseException, InterruptedException {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("majorType", "화학공학·신소재공학부(재수강)");
//                JSONObject jsonObject1 = new JSONObject(hashMap);
//
//                String majorType = String.valueOf(jsonObject1.get("majorType"));
//                if(majorType.contains("재수강")){
//                majorType = majorType.replace("(재수강)", "");
//                }
//                System.out.println(majorType);
//
//                }
//
//        @Test
//        public void handleJsonData2() throws IOException, ParseException, InterruptedException {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("majorType", "화학공학·신소재공학부(재수강)");
//                JSONObject jsonObject1 = new JSONObject(hashMap);
//                String val = "화학공학(비대면-재수강)";
//                int i = val.indexOf("(");
//                System.out.println(i);
//                String substring = val.substring(0, i);
//                String substring2 = val.substring(i+1);
//                System.out.println(substring);
//                System.out.println(substring2);
//                String majorType = String.valueOf(jsonObject1.get("majorType"));
//                if(majorType.contains("재수강")){
//                majorType = majorType.replace("(재수강)", "");
//                }
//                System.out.println(majorType);
//    }
//}
