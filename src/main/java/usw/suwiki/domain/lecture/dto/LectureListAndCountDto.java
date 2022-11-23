package usw.suwiki.domain.lecture.dto;

import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.entity.Lecture;

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
