package Suwikibackend.Suwiki.Json;

import org.json.simple.parser.ParseException;
import usw.suwiki.SuwikiApplication;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.repository.evaluation.JpaEvaluatePostsRepository;
//import usw.suwiki.service.util.JsonToDataTable;
import usw.suwiki.repository.lecture.JpaLectureRepository;
//import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.service.util.JsonToDataTable;

import javax.transaction.Transactional;
import java.io.IOException;

@Transactional
@SpringBootTest(classes = SuwikiApplication.class)
public class JsonTest {

    @Autowired
    JsonToDataTable jsonToDataTable;

    @Test
    public void registration_json() throws IOException, ParseException, InterruptedException {
        jsonToDataTable.toEntity();
    }
}

