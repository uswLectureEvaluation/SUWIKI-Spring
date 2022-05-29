package usw.suwiki.domain.lecture;

import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.Lecture;

import java.util.List;

@Getter
public class LectureListAndCountDto {

    List<Lecture> lectureList;
    Long count;

    @Builder
    public LectureListAndCountDto(List<Lecture> lectureList, Long count) {
        this.lectureList = lectureList;
        this.count = count;
    }

}
