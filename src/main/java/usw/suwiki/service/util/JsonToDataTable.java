package usw.suwiki.service.util;

import usw.suwiki.dto.lecture.JsonToLectureDto;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.repository.lecture.JpaLectureRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

@RequiredArgsConstructor
@Transactional
@Service
public class JsonToDataTable {

    private final JpaLectureRepository lectureRepository;

    public void toEntity() throws IOException, ParseException, InterruptedException {

        Reader reader = new FileReader("/Users/BestFriend/Downloads/Suwiki-Backend/src/main/resources/USW_2022_1 thirteen.json");

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(reader);

        JSONArray jsonArray = (JSONArray) obj;

        if(jsonArray.size() > 0 ){
            for(int i=0; i< jsonArray.size(); i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);


                JsonToLectureDto dto = JsonToLectureDto.builder()
                        .capprType((String) jsonObject.get("capprTypeNm"))
                        .evaluateType((String) jsonObject.get("cretEvalNm"))
                        .lectureCode((String) jsonObject.get("subjtCd"))
                        .semester(jsonObject.get("subjtEstbYear") + "-" + String.valueOf(jsonObject.get("subjtEstbSmrCd")).substring(0, 1))
                        .grade(Integer.parseInt(String.valueOf(jsonObject.get("trgtGrdeCd"))))
                        .lectureType((String) jsonObject.get("facDvnm"))
                        .placeSchedule(String.valueOf(jsonObject.get("timtSmryCn")))
                        .diclNo(String.valueOf(jsonObject.get("diclNo")))
                        .majorType(String.valueOf(jsonObject.get("estbDpmjNm")))
                        .point(Double.parseDouble(String.valueOf(jsonObject.get("point"))))
                        .professor(String.valueOf(jsonObject.get("reprPrfsEnoNm")))
                        .lectureName(String.valueOf(jsonObject.get("subjtNm")))
                        .build();

                Lecture lecture = lectureRepository.findOneBySubAndProf(dto.getLectureName(), dto.getProfessor());
                if (lecture != null) {
                    if (lecture.getSemester().contains(dto.getSemester())) {
                        continue;
                    }else{
                        String updateString = lecture.getSemester() +","+ dto.getSemester();
                        Lecture updatedLecture = Lecture.builder().build();
                        updatedLecture.toEntity(dto);
                        updatedLecture.setSemester(updateString);  //refactoring 필요
                    }
                } else {
                    Lecture savedLecture = Lecture.builder().build();
                    savedLecture.toEntity(dto);
                    Thread.sleep(1);
                    lectureRepository.save(savedLecture);
                }
            }
        }
    }
}