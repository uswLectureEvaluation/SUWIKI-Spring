package usw.suwiki.domain.lecture.service;

import static usw.suwiki.global.exception.ExceptionType.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.global.exception.errortype.LectureException;

@Service
@RequiredArgsConstructor
public class LectureCRUDService {

    private final LectureRepository lectureRepository;

    public Lecture loadLectureFromId(Long id) {
        Optional<Lecture> lecture = lectureRepository.findById(id);
        return validateOptional(lecture);
    }

    public List<String> loadMajorTypes() {
        List<String> majors = lectureRepository.findAllMajorType();
        return majors;
    }

    public Lecture loadLectureFromIdPessimisticLock(Long id) {
        Lecture lecture = lectureRepository.findByIdPessimisticLock(id);
        validateNotNull(lecture);

        return lecture;
    }

    public LecturesAndCountDao loadLectureByKeywordAndOption(String keyword, LectureFindOption option) {
        return lectureRepository.findLectureByFindOption(keyword, option);
    }

    public LecturesAndCountDao loadLectureByKeywordAndMajor(String searchValue, LectureFindOption option) {
        return lectureRepository.findLectureByMajorType(searchValue, option);
    }

    public LecturesAndCountDao loadLecturesByOption(LectureFindOption option) {
        return lectureRepository.findAllLectureByFindOption(option);
    }

    public LecturesAndCountDao loadLecturesByMajor(LectureFindOption option) {
        return lectureRepository.findAllLectureByMajorType(option);
    }

    public void validateNotNull(Lecture lecture) {
        if (lecture == null) {
            throw new LectureException(LECTURE_NOT_FOUND);
        }
    }

    public Lecture validateOptional(Optional<Lecture> lecture) {
        if (lecture.isEmpty()) {
            throw new LectureException(LECTURE_NOT_FOUND);
        }
        return lecture.get();
    }
}
