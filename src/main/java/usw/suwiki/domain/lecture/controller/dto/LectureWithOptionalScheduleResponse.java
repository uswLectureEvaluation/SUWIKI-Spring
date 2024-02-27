package usw.suwiki.domain.lecture.controller.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.LectureSchedule;
import usw.suwiki.domain.lecture.util.LectureStringConverter;
import usw.suwiki.domain.timetable.entity.TimetableCellSchedule;
import usw.suwiki.global.util.apiresponse.ResponseFieldManipulationUtils;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureWithOptionalScheduleResponse {  // TODO refactor V2: lecture_id, schedule_id 분리

    private long id;
    private String name;
    private String type;
    private String major;
    private int grade;
    private String professorName;
    private final List<OriginalLectureCellResponse> originalCellList = new ArrayList<>();

    public static LectureWithOptionalScheduleResponse from(
        final LectureSchedule lectureSchedule
    ) {
        final String professorName = ResponseFieldManipulationUtils
            .resolveLiteralNull(lectureSchedule.getLecture().getProfessor());
        LectureWithOptionalScheduleResponse result = LectureWithOptionalScheduleResponse.builder()
            .id(lectureSchedule.getLecture().getId())
            .name(lectureSchedule.getLecture().getName())
            .professorName(professorName)
            .type(lectureSchedule.getLecture().getType())
            .major(lectureSchedule.getLecture().getMajorType())
            .grade(lectureSchedule.getLecture().getLectureDetail().getGrade())
            .build();

        List<TimetableCellSchedule> scheduleList = LectureStringConverter
            .convertScheduleChunkIntoTimetableCellScheduleList(lectureSchedule.getPlaceSchedule());

        scheduleList.forEach(it -> result.addOriginalCellResponse(OriginalLectureCellResponse.of(it)));
        return result;
    }

    public static LectureWithOptionalScheduleResponse from(
        final Lecture lecture
    ) {
        final String professorName = ResponseFieldManipulationUtils
            .resolveLiteralNull(lecture.getProfessor());

        return LectureWithOptionalScheduleResponse.builder()
            .id(lecture.getId())
            .name(lecture.getName())
            .professorName(professorName)
            .type(lecture.getType())
            .major(lecture.getMajorType())
            .grade(lecture.getLectureDetail().getGrade())
            .build();
    }

    private void addOriginalCellResponse(OriginalLectureCellResponse cellResponse) {
        this.originalCellList.add(cellResponse);
    }
}
