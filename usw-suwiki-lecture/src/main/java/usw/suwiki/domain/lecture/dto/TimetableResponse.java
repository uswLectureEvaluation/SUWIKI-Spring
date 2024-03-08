package usw.suwiki.domain.lecture.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimetableResponse {

    long id;
    int year;
    String semester;
    String name;
    List<TimetableCellResponse> cellList;

    public static TimetableResponse of(Timetable timetable) {
        return builder()
            .id(timetable.getId())
            .year(timetable.getYear())
            .semester(timetable.getSemester().getValue())
            .name(timetable.getName())
            .cellList(
                timetable.getCellList().stream()
                    .map(TimetableCellResponse::of)
                    .toList())
            .build();
    }
}
