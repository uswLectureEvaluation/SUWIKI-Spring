package usw.suwiki.domain.lecture.controller.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.global.util.apiresponse.ResponseFieldManipulationUtils;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureWithScheduleResponse {
    private long id;
    private String name;
    private String type;
    private String major;
    private int grade;
    private String professorName;

    private final List<OriginalLectureCellResponse> originalCellList = new ArrayList<>();

    public static LectureWithScheduleResponse of(
            Lecture lecture
    ) {
        final String professorName = ResponseFieldManipulationUtils.resolveLiteralNull(lecture.getProfessor());
        return LectureWithScheduleResponse.builder()
                .id(lecture.getId())
                .name(lecture.getName())
                .professorName(professorName)
                .type(lecture.getType())
                .major(lecture.getMajorType())
                .grade(lecture.getLectureDetail().getGrade())
                .build();
    }

    public void addOriginalCellResponse(OriginalLectureCellResponse cellResponse) {
        this.originalCellList.add(cellResponse);
    }
}
