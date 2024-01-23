package usw.suwiki.lectureutill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.service.LectureService;

// TODO fix: 테스트에서 제거
// TODO: 다른 테스트 코드들에도 "test" 프로파일 설정 혹은 default 프로파일로 변경. ActiveProfile 필요성 고민
@ActiveProfiles("test")
@SpringBootTest
class InsertNewLectureTest {

    @Autowired
    LectureService lectureService;

    @Autowired
    LectureRepository lectureRepository;

    @Test
    void insertNewLecture() {
        lectureService.bulkSaveJsonLectures("src/main/resources/USW_2023_2.json");
    }
}