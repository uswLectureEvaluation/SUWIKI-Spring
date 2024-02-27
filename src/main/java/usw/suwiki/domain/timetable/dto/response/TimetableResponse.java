package usw.suwiki.domain.timetable.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.timetable.entity.Timetable;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TimetableResponse {

    long id;
    int year;
    String semester;
    String name;
    List<TimetableCellResponse> cellList;

    public static TimetableResponse of(Timetable timetable) {
        return TimetableResponse.builder()
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
