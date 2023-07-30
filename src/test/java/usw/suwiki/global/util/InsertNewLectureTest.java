package usw.suwiki.global.util;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;

import java.io.IOException;

// @ActiveProfiles("test")
@SpringBootTest
class InsertNewLectureTest {

    @Autowired
    JsonToDataTable jsonToDataTable;

    @Autowired
    LectureRepository lectureRepository;

    @Test
    void insertNewLecture() throws IOException, ParseException, InterruptedException {
        jsonToDataTable.toEntity("src/main/resources/USW_2023_2.json");
    }
}