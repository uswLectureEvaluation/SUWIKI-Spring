package usw.suwiki.domain.lecture.controller.dto;

import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.domain.Lecture;

import java.util.List;

@Getter
public class LecturesAndCountDto {

    List<Lecture> lectureList;
    Long count;

    @Builder
    public LecturesAndCountDto(List<Lecture> lectureList, Long count) {
        this.lectureList = lectureList;
        this.count = count;
    }

}
