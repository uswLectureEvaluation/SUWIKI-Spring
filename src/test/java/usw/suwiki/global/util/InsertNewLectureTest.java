package usw.suwiki.global.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import usw.suwiki.domain.lecture.domain.repository.LectureRepository;

@ActiveProfiles("test")
@SpringBootTest
class InsertNewLectureTest {

	@Autowired
	JsonToDataTable jsonToDataTable;

	@Autowired
	LectureRepository lectureRepository;

	@Test
	void insertNewLecture() throws IOException, ParseException, InterruptedException {
		jsonToDataTable.toEntity("src/main/resources/USW_2023_1.json");
	}
}