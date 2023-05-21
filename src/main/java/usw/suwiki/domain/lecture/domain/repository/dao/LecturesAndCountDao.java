package usw.suwiki.domain.lecture.domain.repository.dao;

import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.domain.Lecture;

import java.util.List;

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
