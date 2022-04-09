package usw.suwiki.repository.lecture;

import usw.suwiki.dto.lecture.LectureFindOption;
import usw.suwiki.domain.lecture.Lecture;
import org.springframework.stereotype.Repository;
import usw.suwiki.dto.lecture.LectureListAndCountDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaLectureRepository implements LectureRepository {
    private final EntityManager em;

    public JpaLectureRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Lecture lecture) {
        em.persist(lecture);
    }

    @Override
    public Lecture findById(Long id) {
        Lecture lecture = em.find(Lecture.class, id);
        return lecture;
    }

    @Override
    public Lecture findOneBySubAndProf(String lectureNameName, String professorName) {
        List<Lecture> resultList = em.createQuery("SELECT l FROM Lecture l WHERE(l.lectureName =:lectureName AND l.professor =:professor)").setParameter("lectureName", lectureNameName)
                .setParameter("professor", professorName)
                .getResultList();     //refactoring 해야한다
        if (resultList.isEmpty()) {
            return null;
        } else return resultList.get(0);
    }

    @Override
    public LectureListAndCountDto findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption) {
        Optional<String> orderOption = lectureFindOption.getOrderOption();
        Optional<Integer> pageNumber = lectureFindOption.getPageNumber();
        if (pageNumber.isEmpty()) {
            pageNumber = Optional.of(1);
        }
        if (orderOption.isEmpty()) {
            orderOption = Optional.of("modifiedDate");
        }

        String[] orderOptions = {"modifiedDate",
                "lectureSatisfactionAvg",
                "lectureHoneyAvg",
                "lectureLearningAvg",
                "lectureTotalAvg"};

        if (!Arrays.asList(orderOptions).contains(orderOption.get())) {
            throw new InvalidParameterException("invalid orderOption");
        }

        String query = "SELECT l FROM Lecture l ";
        query += String.format("WHERE l.lectureName LIKE CONCAT('%%',UPPER(:value),'%%') OR l.professor LIKE CONCAT('%%',UPPER(:value),'%%') ORDER BY l.%s DESC", orderOption.get());

        List<Lecture> lectureList = em.createQuery(query, Lecture.class)
                .setParameter("value", searchValue)
                .setFirstResult((pageNumber.get()-1)*10)
                .setMaxResults(10)
                .getResultList();

        List countList = em.createQuery("SELECT COUNT(l) FROM Lecture l WHERE l.lectureName LIKE CONCAT('%%',UPPER(:value),'%%') OR l.professor LIKE CONCAT('%%',UPPER(:value),'%%')")
                .setParameter("value", searchValue)
                .getResultList();
        Long count = (Long) countList.get(0);

        return LectureListAndCountDto.builder().lectureList(lectureList).count(count).build();
    }

    @Override
    public LectureListAndCountDto findAllLectureByFindOption(LectureFindOption lectureFindOption) {
        Optional<String> orderOption = lectureFindOption.getOrderOption();
        Optional<Integer> pageNumber = lectureFindOption.getPageNumber();
        if (pageNumber.isEmpty()) {
            pageNumber = Optional.of(1);
        }
        if (orderOption.isEmpty()) {
            orderOption = Optional.of("modifiedDate");
        }

        String[] orderOptions = {"modifiedDate",
                "lectureSatisfactionAvg",
                "lectureHoneyAvg",
                "lectureLearningAvg",
                "lectureTotalAvg"};

//        String[] sortOptions = {"asc", "desc"};

        if (!Arrays.asList(orderOptions).contains(orderOption.get())) {
            throw new InvalidParameterException("invalid orderOption");
        }

        String query = "SELECT l FROM Lecture l ";
        query += String.format("ORDER BY l.%s DESC", orderOption.get());

        List<Lecture> lectureList = em.createQuery(query, Lecture.class)
                .setFirstResult((pageNumber.get()-1)*10)
                .setMaxResults(10)
                .getResultList();

        List countList = em.createQuery("SELECT COUNT(l) FROM Lecture l")
                .getResultList();
        Long count = (Long) countList.get(0);

        return LectureListAndCountDto.builder().lectureList(lectureList).count(count).build();
    }

}