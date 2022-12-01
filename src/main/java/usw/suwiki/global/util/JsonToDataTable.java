package usw.suwiki.global.util;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.lecture.dto.JsonToLectureDto;
import usw.suwiki.domain.lecture.entity.Lecture;
import usw.suwiki.domain.lecture.repository.LectureRepository;

import javax.transaction.Transactional;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

@RequiredArgsConstructor
@Transactional
@Service
public class JsonToDataTable {

    private final LectureRepository lectureRepository;

    //이상한 강의명 예외 처리 로직.
    private JsonToLectureDto handleLectureNameException(JsonToLectureDto dto) {

        if (dto.getLectureName().contains("재수강-")) {
            int index = dto.getLectureName().indexOf("(");
            String lectureName = dto.getLectureName().substring(0, index);
            dto.setLectureName(lectureName);
        }

        if (dto.getLectureName().contains("재수강")) {
            dto.setLectureName(dto.getLectureName().replace("(재수강)", ""));
            dto.setLectureName(dto.getLectureName().replace("재수강", ""));
        }

        if (dto.getLectureName().contains("비대면수업")) {
            dto.setLectureName(dto.getLectureName().replace("(비대면수업)", ""));
            dto.setLectureName(dto.getLectureName().replace("비대면수업-", ""));
            dto.setLectureName(dto.getLectureName().replace("비대면수업_", ""));
            dto.setLectureName(dto.getLectureName().replace("비대면수업", ""));
        }
        if (dto.getLectureName().contains("대면수업")) {
            dto.setLectureName(dto.getLectureName().replace("(대면수업)", ""));
            dto.setLectureName(dto.getLectureName().replace("대면수업-", ""));
            dto.setLectureName(dto.getLectureName().replace("대면수업_", ""));
            dto.setLectureName(dto.getLectureName().replace("대면수업", ""));
        }
        if (dto.getLectureName().contains("혼합수업")) {
            dto.setLectureName(dto.getLectureName().replace("(혼합수업)", ""));
            dto.setLectureName(dto.getLectureName().replace("혼합수업-", ""));
            dto.setLectureName(dto.getLectureName().replace("혼합수업_", ""));
            dto.setLectureName(dto.getLectureName().replace("혼합수업", ""));
        }

        return dto;
    }

    public void toEntity(String path) throws IOException, ParseException, InterruptedException {

        Reader reader = new FileReader(path);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(reader);

        JSONArray jsonArray = (JSONArray) obj;

        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                JsonToLectureDto dto = JsonToLectureDto.builder()
                        .capprType((String) jsonObject.get("capprTypeNm"))
                        .evaluateType((String) jsonObject.get("cretEvalNm"))
                        .lectureCode((String) jsonObject.get("subjtCd"))
                        .selectedSemester(jsonObject.get("subjtEstbYear") + "-" + String.valueOf(jsonObject.get("subjtEstbSmrCd")).substring(0, 1))
                        .grade(Integer.parseInt(jsonObject.get("trgtGrdeCd").toString()))
                        .lectureType((String) jsonObject.get("facDvnm"))
                        .placeSchedule(String.valueOf(jsonObject.get("timtSmryCn")))
                        .diclNo(String.valueOf(jsonObject.get("diclNo")))
                        .majorType(String.valueOf(jsonObject.get("estbDpmjNm")))
                        .point(Double.parseDouble(String.valueOf(jsonObject.get("point"))))
                        .professor(String.valueOf(jsonObject.get("reprPrfsEnoNm")))
                        .lectureName(String.valueOf(jsonObject.get("subjtNm")))
                        .build();

                //professor 없으면 "-" 로 채움 (null 값 들어가지 않게)
                if (dto.getProfessor().isEmpty() || dto.getProfessor() == null) {
                    dto.setProfessor("-");
                }

                //handleException
                dto = handleLectureNameException(dto);

                //"·" to replace "-"
                if (dto.getMajorType().contains("·")) {
                    String majorType = dto.getMajorType();
                    majorType = majorType.replace("·", "-");
                    dto.setMajorType(majorType);
                }

                Lecture lecture = lectureRepository.verifyJsonLecture(dto.getLectureName(), dto.getProfessor(), dto.getMajorType());

                if (lecture != null) {
                    if (!lecture.getSemesterList().contains(dto.getSelectedSemester())) {
                        String updateString = lecture.getSemesterList() + ", " + dto.getSelectedSemester();
                        lecture.setSemester(updateString);  //refactoring 필요
                        lectureRepository.save(lecture);
                    }
                } else if (lecture == null) {
                    Lecture savedLecture = Lecture.builder().build();
                    savedLecture.toEntity(dto);
                    Thread.sleep(1);
                    lectureRepository.save(savedLecture);
                }
            }
        }
    }
}