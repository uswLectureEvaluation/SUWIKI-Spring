package usw.suwiki.domain.lecture;

import lombok.Builder;
import lombok.Getter;

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
