package usw.suwiki.domain.lecture.domain.repository;

import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.controller.dto.LecturesAndCountDto;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.exception.ErrorType;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class JpaLectureRepository implements LectureRepository {
    private final EntityManager em;
    private final String DEFAULT_ORDER = "modifiedDate";
    private final Integer DEFAULT_PAGE = 1;

    private final String[] orderOptions = {"modifiedDate",
        "lectureEvaluationInfo.lectureSatisfactionAvg",
        "lectureEvaluationInfo.lectureHoneyAvg",
        "lectureEvaluationInfo.lectureLearningAvg",
        "lectureEvaluationInfo.lectureTotalAvg"};

    public JpaLectureRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Lecture lecture) {
        em.persist(lecture);
    }

    @Override
    public Lecture findByIdPessimisticLock(Long id) {
        Lecture lecture = em.find(Lecture.class, id, LockModeType.PESSIMISTIC_WRITE);
        return lecture;
    }

    @Override
    public Lecture findById(Long id) {
        Lecture lecture = em.find(Lecture.class, id);
        return lecture;
    }

    @Override
    public Lecture verifyJsonLecture(String lectureName, String professorName, String majorType) {
        List<Lecture> results = em.createQuery(
                "SELECT l FROM Lecture l WHERE(l.name =:lectureName AND l.professor =:professor AND l.majorType = :majorType)")
            .setParameter("lectureName", lectureName)
            .setParameter("professor", professorName)
            .setParameter("majorType", majorType)
            .getResultList();     //refactoring 해야한다
        if (results.isEmpty()) {
            return null;
        } else return results.get(0);
    }

    @Override
    public LecturesAndCountDto findLectureByFindOption(String searchValue, LectureFindOption option) {
        String orderOption = initializeOrderOption(option.getOrderOption());
        Integer page = initializePageNumber(option.getPageNumber());

        if (!Arrays.asList(orderOptions).contains(orderOption)) {
            throw new AccountException(ErrorType.INVALID_ORDER_OPTION);
        }

        String query = String.format("SELECT l FROM Lecture l "
            + "WHERE l.name LIKE CONCAT('%%',UPPER(:value),'%%') OR "
            + "l.professor LIKE CONCAT('%%',UPPER(:value),'%%') "
            + "ORDER BY CASE WHEN (l.postsCount > 0) THEN 1 ELSE 2 END, l.%s DESC", orderOption);

        List<Lecture> results = em.createQuery(query, Lecture.class)
            .setParameter("value", searchValue)
            .setFirstResult((page - 1) * 10)
            .setMaxResults(10)
            .getResultList();

        List lectures = em.createQuery(
                "SELECT COUNT(l) FROM Lecture l WHERE l.name LIKE CONCAT('%%',UPPER(:value),'%%') OR "
                    + "l.professor LIKE CONCAT('%%',UPPER(:value),'%%')")
            .setParameter("value", searchValue)
            .getResultList();
        Long count = (Long) lectures.get(0);

        return LecturesAndCountDto.builder()
            .lectureList(results).count(count)
            .build();
    }

    @Override
    public LecturesAndCountDto findAllLectureByFindOption(LectureFindOption option) {
        String orderOption = initializeOrderOption(option.getOrderOption());
        Integer page = initializePageNumber(option.getPageNumber());

        if (!Arrays.asList(orderOptions).contains(orderOption)) {
            throw new AccountException(ErrorType.INVALID_ORDER_OPTION);
        }

        String query = String.format("SELECT l FROM Lecture l "
            + "ORDER BY CASE WHEN (l.postsCount > 0) THEN 1 ELSE 2 END, l.%s DESC", orderOption);

        List<Lecture> results = em.createQuery(query, Lecture.class)
            .setFirstResult((page - 1) * 10)
            .setMaxResults(10)
            .getResultList();

        List lectures = em.createQuery("SELECT COUNT(l) FROM Lecture l")
                .getResultList();
        Long count = (Long) lectures.get(0);

        return LecturesAndCountDto.builder().lectureList(results).count(count).build();
    }

    @Override
    public LecturesAndCountDto findLectureByMajorType(String searchValue, LectureFindOption option) {
        String orderOption = initializeOrderOption(option.getOrderOption());
        Integer page = initializePageNumber(option.getPageNumber());

        if (!Arrays.asList(orderOptions).contains(orderOption)) {
            throw new AccountException(ErrorType.INVALID_ORDER_OPTION);
        }

        String majorType = option.getMajorType();

        String query = String.format("SELECT l FROM Lecture l "
            + "WHERE l.majorType = :major AND "
            + "(l.name LIKE CONCAT('%%',UPPER(:value),'%%') "
            + "OR l.professor LIKE CONCAT('%%',UPPER(:value),'%%')) "
            + "ORDER BY CASE WHEN (l.postsCount > 0) THEN 1 ELSE 2 END, l.%s DESC", orderOption);

        List<Lecture> results = em.createQuery(query, Lecture.class)
            .setParameter("major", majorType)
            .setParameter("value", searchValue)
            .setFirstResult((page - 1) * 10)
            .setMaxResults(10)
            .getResultList();

        List lectures = em.createQuery("SELECT COUNT(l) FROM Lecture l WHERE l.majorType = :major AND "
                + "(l.name LIKE CONCAT('%%',UPPER(:value),'%%') OR l.professor LIKE CONCAT('%%',UPPER(:value),'%%'))")
            .setParameter("major", majorType)
            .setParameter("value", searchValue)
            .getResultList();
        Long count = (Long) lectures.get(0);

        return LecturesAndCountDto.builder().lectureList(results).count(count).build();
    }

    @Override
    public LecturesAndCountDto findAllLectureByMajorType(LectureFindOption option) {
        String orderOption = initializeOrderOption(option.getOrderOption());
        Integer page = initializePageNumber(option.getPageNumber());

        if (!Arrays.asList(orderOptions).contains(orderOption)) {
            throw new AccountException(ErrorType.INVALID_ORDER_OPTION);
        }

        String majorType = option.getMajorType();

        String query = String.format("SELECT l FROM Lecture l "
            + "WHERE l.majorType = :major "
            + "ORDER BY CASE WHEN (l.postsCount > 0) THEN 1 ELSE 2 END, l.%s DESC", orderOption);

        List<Lecture> results = em.createQuery(query, Lecture.class)
            .setParameter("major", majorType)
            .setFirstResult((page - 1) * 10)
            .setMaxResults(10)
            .getResultList();

        List lectures = em.createQuery("SELECT COUNT(l) FROM Lecture l WHERE l.majorType = :major")
                .setParameter("major", majorType)
                .getResultList();
        Long count = (Long) lectures.get(0);

        return LecturesAndCountDto.builder().lectureList(results).count(count).build();
    }

    @Override
    public List<String> findAllMajorType() {
        List<String> resultList = em.createQuery("SELECT DISTINCT l.majorType FROM Lecture l")
                .getResultList();

        return resultList;
    }

    private String initializeOrderOption(String option) {
        if (option == null) {
            option = DEFAULT_ORDER;
        }
        return option;
    }

    private Integer initializePageNumber(Integer page) {
        if (page == null) {
            page = DEFAULT_PAGE;
        }
        return page;
    }
}