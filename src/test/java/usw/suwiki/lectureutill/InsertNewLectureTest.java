package usw.suwiki.lectureutill;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.global.util.loadjson.JsonToDataTable;

// TODO: 다른 테스트 코드들에도 "test" 프로파일 설정 혹은 default 프로파일로 변경. ActiveProfile 필요성 고민
@ActiveProfiles("test")
@SpringBootTest
class InsertNewLectureTest {

    @Autowired
    JsonToDataTable jsonToDataTable;

    @Autowired
    LectureRepository lectureRepository;

    @Test
    void insertNewLecture() throws IOException, ParseException {
        jsonToDataTable.toEntity("src/main/resources/USW_2023_2.json");
    }
}