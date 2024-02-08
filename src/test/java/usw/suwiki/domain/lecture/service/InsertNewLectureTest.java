package usw.suwiki.domain.lecture.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;


@SpringBootTest
@Disabled   // WARN: 통합 테스트에 포함되어선 안 됩니다.
class InsertNewLectureTest {

    @Autowired
    LectureService lectureService;

    @Autowired
    LectureRepository lectureRepository;

    @Test
    void insertNewLecture() {
        lectureService.bulkApplyLectureJsonFile("src/main/resources/USW_2024_1.json");
    }
}