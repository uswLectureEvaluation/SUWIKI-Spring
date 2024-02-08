package usw.suwiki.domain.lecture.controller.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.domain.LectureSchedule;
import usw.suwiki.global.util.apiresponse.ResponseFieldManipulationUtils;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureWithScheduleResponse {  // TODO refactor V2: lecture_id, schedule_id 분리
    private long id;
    private String name;
    private String type;
    private String major;
    private int grade;
    private String professorName;

    private final List<OriginalLectureCellResponse> originalCellList = new ArrayList<>();

    public static LectureWithScheduleResponse of(
            LectureSchedule lectureSchedule
    ) {
        final String professorName = ResponseFieldManipulationUtils.resolveLiteralNull(lectureSchedule.getLecture().getProfessor());
        return LectureWithScheduleResponse.builder()
                .id(lectureSchedule.getId())
                .name(lectureSchedule.getLecture().getName())
                .professorName(professorName)
                .type(lectureSchedule.getLecture().getType())
                .major(lectureSchedule.getLecture().getMajorType())
                .grade(lectureSchedule.getLecture().getLectureDetail().getGrade())
                .build();
    }

    public void addOriginalCellResponse(OriginalLectureCellResponse cellResponse) {
        this.originalCellList.add(cellResponse);
    }
}
