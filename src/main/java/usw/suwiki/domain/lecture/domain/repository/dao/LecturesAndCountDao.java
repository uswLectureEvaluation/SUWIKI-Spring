package usw.suwiki.domain.lecture.domain.repository.dao;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.domain.Lecture;

@Getter
public class LecturesAndCountDao {

    List<Lecture> lectureList;
    Long count;

    @Builder
    public LecturesAndCountDao(List<Lecture> lectureList, Long count) {
        this.lectureList = lectureList;
        this.count = count;
    }
}
