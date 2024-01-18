package usw.suwiki.global.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.lecture.controller.dto.JsonToLectureForm;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;

@Service
@Transactional
@RequiredArgsConstructor
// TODO refactor: Json - JsonToLectureForm 변환 책임 분리
// TODO feat: 데이터 적재 과정을 admin API로 생성
public class JsonToDataTable {

    private final LectureRepository lectureRepository;



    // JSON File path -> 강의 데이터 변환
    // TODO style: 메서드명 변경
    // TODO refactor: try catch문. try resource문도 가능한가? File이면.
    public void toEntity(String path) throws IOException, ParseException, InterruptedException {

        Reader reader = new FileReader(path);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(reader);

        JSONArray jsonArray = (JSONArray) obj;

        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                // TODO sytle: dto -> vo
                JsonToLectureForm dto = JsonToLectureForm.builder()
                        // TODO refactor: 매직 리터럴 상수화
                        .capprType((String) jsonObject.get("capprTypeNm"))
                        .evaluateType((String) jsonObject.get("cretEvalNm"))
                        .lectureCode((String) jsonObject.get("subjtCd"))
                        .selectedSemester(jsonObject.get("subjtEstbYear") + "-" + String.valueOf(
                                jsonObject.get("subjtEstbSmrCd")).charAt(0))
                        // 이거 이미 JSON에서 int 아닌가?
                        .grade(Integer.parseInt(jsonObject.get("trgtGrdeCd").toString()))
                        .lectureType((String) jsonObject.get("facDvnm"))

                        // TODO fix: 강의 장소-교시 컬럼 (place_schedule) 누락
                        // TODO fix: place_schedule - 강의 장소 스트링 누락
                        .placeSchedule(String.valueOf(jsonObject.get("timtSmryCn")))
                        .diclNo(String.valueOf(jsonObject.get("diclNo")))
                        .majorType(String.valueOf(jsonObject.get("estbDpmjNm")))
                        .point(Double.parseDouble(String.valueOf(jsonObject.get("point"))))
                        .professor(String.valueOf(jsonObject.get("reprPrfsEnoNm")))
                        .lectureName(String.valueOf(jsonObject.get("subjtNm")))
                        .build();

                /**
                 * TODO refactor:
                 * 기존의 강의가 있다면 해당 강의로. 강의의 식별자는 강의명 + 교수명 + 전공 타입
                 * 음... 만약에 같은 전공학과에 같은 이름의 교수가 둘 있다면?
                 * JSONData의 professor 식별 번호가 있다. (추정) 이걸로 하는게 낫다고 생각.
                 * 문제점: DB 스키마를 변경해야 함. 강의에 교수번호를 붙여야 하는데... 정규화 위배..
                 * 교수 테이블을 분리하는게 낫다고 생각.
                 */

                Optional<Lecture> optionalExistingLecture = lectureRepository.verifyJsonLecture(
                        dto.getLectureName(),
                        dto.getProfessor(),
                        dto.getMajorType()
                );

                if (optionalExistingLecture.isPresent()) {
                    Lecture lecture = optionalExistingLecture.get();
                    if (lecture.getType() == null || lecture.getType().isEmpty()) {
                        lecture.setType(dto.getLectureType());
                    }

                    /**
                     * TODO think:
                     * Lecture은 잘 저장되지만, 많은 수의 place_schedule들이 누락되고 있다.
                     */
                    if (!lecture.getSemester().contains(dto.getSelectedSemester())) {
                        String updateString =
                                lecture.getSemester() + ", " + dto.getSelectedSemester();
                        lecture.setSemester(updateString);  //refactoring 필요
                        lectureRepository.save(lecture);
                    }
                } else {
                    // TODO refactor: JsonToLectureForm -> Lecture 의존하도록
                    Lecture savedLecture = Lecture.toEntity(dto);
                    Thread.sleep(1);    // TODO figure: 왜 필요하지?
                    lectureRepository.save(savedLecture);
                }

            }
        }
    }
}