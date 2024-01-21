package usw.suwiki.global.util.loadjson;

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
    // TODO refactor: throws -> try catch
    // TODO fix: 강의 장소-교시 컬럼 (place_schedule) 누락
    public void toEntity(String path) throws IOException, ParseException, InterruptedException {
        Reader reader = new FileReader(path);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(reader);

        JSONArray jsonArray = (JSONArray) obj;

        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                JSONLectureVO jsonLectureVO = new JSONLectureVO(jsonObject);

                Optional<Lecture> optionalLecture = lectureRepository.verifyJsonLecture(
                        jsonLectureVO.getLectureName(),
                        jsonLectureVO.getProfessor(),
                        jsonLectureVO.getMajorType()
                );

                if (optionalLecture.isPresent()) {
                    Lecture lecture = optionalLecture.get();

                    lecture.fixOmission(jsonLectureVO);
                    lecture.addSemester(jsonLectureVO);

                    lectureRepository.save(lecture);
                } else {
                    // TODO refactor: JsonToLectureForm -> Lecture 의존하도록
                    Lecture newLecture = jsonLectureVO.toEntity();
                    Thread.sleep(1);    // TODO figure: 왜 필요하지?
                    lectureRepository.save(newLecture);
                }

            }
        }
    }
}