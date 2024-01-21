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
    // TODO fix: place_schedule - 강의 장소 스트링 누락
    public void toEntity(String path) throws IOException, ParseException, InterruptedException {
        Reader reader = new FileReader(path);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(reader);

        JSONArray jsonArray = (JSONArray) obj;

        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                JSONLectureVO jsonLectureVO = new JSONLectureVO(jsonObject);

                /**
                 * TODO refactor:
                 * 기존의 강의가 있다면 해당 강의로. 강의의 식별자는 강의명 + 교수명 + 전공 타입
                 * 음... 만약에 같은 전공학과에 같은 이름의 교수가 둘 있다면?
                 * JSONData의 professor 식별 번호가 있다. (추정) 이걸로 하는게 낫다고 생각.
                 * 문제점: DB 스키마를 변경해야 함. 강의에 교수번호를 붙여야 하는데... 정규화 위배..
                 * 교수 테이블을 분리하는게 낫다고 생각.
                 */

                /**
                 * TODO think:
                 * Lecture은 잘 조회되지만, 이 과정에서 많은 수의 place_schedule들이 누락되고 있다.
                 * "강의" 조회는 별 문제 없겠지만 시간표용 강의 조회로써는 그닥이다. 결국 학생들이 스케줄을 수정해야 한다.
                 * PlaceSchedule을 별도 테이블에 관리를 해야 할지..
                 */
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
                    Lecture savedLecture = Lecture.toEntity(jsonLectureVO);
                    Thread.sleep(1);    // TODO figure: 왜 필요하지?
                    lectureRepository.save(savedLecture);
                }

            }
        }
    }
}