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
// TODO refactor: LectureService로 이동
// TODO feat: 데이터 적재 과정을 admin API로 생성
public class JsonToDataTable {

    private final LectureRepository lectureRepository;

    // TODO refactor: throws -> try catch
    public void bulkSaveJsonLectures(String filePath) throws IOException, ParseException {
        Reader reader = new FileReader(filePath);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(reader);

        JSONArray jsonArray = (JSONArray) obj;
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                JSONLectureVO jsonLectureVO = new JSONLectureVO(jsonObject);

                Optional<Lecture> optionalLecture = lectureRepository.findByExtraUniqueKey(
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
                    Lecture newLecture = jsonLectureVO.toEntity();
                    lectureRepository.save(newLecture);
                }

            }
        }
    }
}