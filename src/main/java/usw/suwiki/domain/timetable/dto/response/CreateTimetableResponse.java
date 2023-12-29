package usw.suwiki.domain.timetable.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.timetable.entity.Timetable;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateTimetableResponse {
    long id;
    int year;
    String semester;
    String name;

    public static CreateTimetableResponse from(Timetable timetable) {
        return CreateTimetableResponse.builder()
                .id(timetable.getId())
                .year(timetable.getYear())
                .semester(timetable.getSemester().getValue())
                .name(timetable.getName())
                .build();
    }
}
