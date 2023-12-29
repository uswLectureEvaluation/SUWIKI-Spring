package usw.suwiki.domain.timetable.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.timetable.entity.Timetable;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimetableResponse {
    long id;
    int year;
    String semester;
    String name;

    public static TimetableResponse from(Timetable timetable) {
        return TimetableResponse.builder()
                .id(timetable.getId())
                .year(timetable.getYear())
                .semester(timetable.getSemester().getValue())
                .name(timetable.getName())
                .build();
    }
}
