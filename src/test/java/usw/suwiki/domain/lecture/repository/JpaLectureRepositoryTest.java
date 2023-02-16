package usw.suwiki.domain.lecture.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import usw.suwiki.domain.lecture.LectureFindOption;
import usw.suwiki.domain.lecture.dto.LectureListAndCountDto;
import usw.suwiki.domain.lecture.entity.Lecture;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.testconfig.TestConfig;

@SpringBootTest
class JpaLectureRepositoryTest {

    @Autowired
    JpaLectureRepository lectureRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("FindOption 을 통해 Lecture 가지고 오기 정상 동작 테스트")
    void findLectureByFindOptionTest() {
        //given
        String searchValue = "과학적";
        LectureFindOption option = new LectureFindOption(Optional.empty(), Optional.of(1), Optional.empty());
        LectureListAndCountDto response = lectureRepository.findLectureByFindOption(searchValue, option);

        for (Lecture lecture : response.getLectureList()) {
            System.out.println(lecture.getPostsCount());
        }
    }
}