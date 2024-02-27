package usw.suwiki.domain.lecture.service;

import static usw.suwiki.global.exception.ExceptionType.LECTURE_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;
import usw.suwiki.global.exception.errortype.LectureException;

@Service
@RequiredArgsConstructor
public class LectureCRUDService {

    private final LectureRepository lectureRepository;

    public List<String> loadMajorTypes() {
        List<String> majors = lectureRepository.findAllMajorType();
        return majors;
    }

    public Lecture loadLectureFromIdPessimisticLock(Long id) {
        return lectureRepository.findForUpdateById(id)
            .orElseThrow(() -> new LectureException(LECTURE_NOT_FOUND));
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

}
