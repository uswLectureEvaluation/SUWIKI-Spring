package usw.suwiki.lectureutill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.service.LectureService;

// WARN: 통합 테스트에 포함되어선 안 됩니다.
@SpringBootTest
class InsertNewLectureTest {

    @Autowired
    LectureService lectureService;

    @Autowired
    LectureRepository lectureRepository;

    // TODO: 파일 4개 운영 DB에 반영 -> 해당 테스트 전체 주석 처리
    @Test
    void insertNewLecture() {
        lectureService.bulkSaveJsonLectures("src/main/resources/USW_2023_2.json");
    }
}