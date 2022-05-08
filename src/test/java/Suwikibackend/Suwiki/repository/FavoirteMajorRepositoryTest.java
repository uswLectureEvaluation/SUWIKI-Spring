package Suwikibackend.Suwiki.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.SuwikiApplication;
import usw.suwiki.repository.favorite_major.JpaFavoriteMajorRepository;
import usw.suwiki.repository.lecture.JpaLectureRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@SpringBootTest(classes = SuwikiApplication.class)
public class FavoirteMajorRepositoryTest {
    @Autowired
    JpaFavoriteMajorRepository jpaFavoriteMajorRepository;

    @Test
    public void findOnlyMajorTypeByUser(){
        List<String> list = jpaFavoriteMajorRepository.findOnlyMajorTypeByUser(17L);

        System.out.println(list);
    }
}
