package usw.suwiki.domain.lecture.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.common.response.ResponseFieldManipulationUtils;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.data.LectureStringConverter;
import usw.suwiki.domain.lecture.schedule.LectureSchedule;
import usw.suwiki.domain.lecture.timetable.TimetableCellSchedule;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LectureWithOptionalScheduleResponse {  // TODO refactor V2: lecture_id, schedule_id 분리

    private long id;
    private String name;
    private String type;
    private String major;
    private int grade;
    private String professorName;
    private final List<OriginalLectureCellResponse> originalCellList = new ArrayList<>();

    public static LectureWithOptionalScheduleResponse from(LectureSchedule lectureSchedule) {
        final String professorName = ResponseFieldManipulationUtils
            .resolveLiteralNull(lectureSchedule.getLecture().getProfessor());

        LectureWithOptionalScheduleResponse result = builder()
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

    public static LectureWithOptionalScheduleResponse from(final Lecture lecture) {
        return builder()
            .id(lecture.getId())
            .name(lecture.getName())
            .professorName(ResponseFieldManipulationUtils.resolveLiteralNull(lecture.getProfessor()))
            .type(lecture.getType())
            .major(lecture.getMajorType())
            .grade(lecture.getLectureDetail().getGrade())
            .build();
    }

    private void addOriginalCellResponse(OriginalLectureCellResponse cellResponse) {
        this.originalCellList.add(cellResponse);
    }
}
