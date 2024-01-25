package usw.suwiki.domain.lecture.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import usw.suwiki.domain.lecture.controller.dto.LectureWithScheduleResponse;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.template.LectureDetailTemplate;
import usw.suwiki.domain.lecture.template.LectureTemplate;
import usw.suwiki.global.annotation.SuwikiMockitoTest;
import usw.suwiki.global.dto.NoOffsetPaginationResponse;

@SuwikiMockitoTest
public class LectureServiceTest {
    @InjectMocks
    LectureService lectureService;

    @Mock
    LectureRepository lectureRepository;

    @Value("${business.current-semester}")
    private String currentSemester;

    public static final long SPYING_ID = 123712L;

    @Test
    @DisplayName("시간표 생성시 강의 검색 - 이번 학기에 열린 강의 리스트 조회")
    public void SELECT_LECTURE_LIST_TIMETABLE_THIS_SEMESTER() {
        // given
        Lecture multipleLocationLecture = spy(LectureTemplate.createDummyLecture(
                currentSemester,
                "물리학및실험2",
                "전교",
                "전자재료공학부",
                "전계진",
                LectureDetailTemplate.varyPlaceSchedule("미래103(화1,2),미래B102(화3,4)")     // 스케줄 2개
        ));
        Lecture multipleDayLecture = spy(LectureTemplate.createDummyLecture(
                currentSemester,
                "관현악합주6",
                "전핵",
                "관현악과",
                "박태영",
                LectureDetailTemplate.varyPlaceSchedule("음악109(월5,6 화5,6 수5,6)")        // 스케줄 3개
        ));
        Lecture multipleLocationAndDayLecture = spy(LectureTemplate.createDummyLecture(
                currentSemester,
                "역대급헬강의",
                "전핵",
                "역대급학부",
                "역대급교수",
                LectureDetailTemplate.varyPlaceSchedule("미래103(월1,2 화1,2),미래B102(월7,8 화7,8)")   // 스케줄 4개
        ));
        Lecture unconnectedPeriodsLecture = spy(LectureTemplate.createDummyLecture(
                currentSemester,
                "건축설계2",
                "전핵",
                "건축학",
                "최기원",
                LectureDetailTemplate.varyPlaceSchedule("2공학207(목1,2,3,5,6,7)")     // 스케줄 2개
        ));

        List<Lecture> lectureList = List.of(
                multipleLocationLecture,
                multipleDayLecture,
                multipleLocationAndDayLecture,
                unconnectedPeriodsLecture
        );
        Slice<Lecture> queryResult = new SliceImpl<>(lectureList);
        given(lectureRepository.findCurrentSemesterLectures(anyLong(), anyInt(), any(), any(), any()))
                .willReturn(queryResult);

        when(multipleLocationLecture.getId()).thenReturn(SPYING_ID);
        when(multipleDayLecture.getId()).thenReturn(SPYING_ID);
        when(multipleLocationAndDayLecture.getId()).thenReturn(SPYING_ID);
        when(unconnectedPeriodsLecture.getId()).thenReturn(SPYING_ID);

        // when
        NoOffsetPaginationResponse<LectureWithScheduleResponse> response = lectureService
                .findPagedLecturesWithSchedule(0L, 20, null, null, null);

        // then
        assertThat(response.getIsLast()).isTrue();
        assertThat(response.getContent().get(0).getOriginalCellList().size()).isEqualTo(2);
        assertThat(response.getContent().get(1).getOriginalCellList().size()).isEqualTo(3);
        assertThat(response.getContent().get(2).getOriginalCellList().size()).isEqualTo(4);
        assertThat(response.getContent().get(3).getOriginalCellList().size()).isEqualTo(2);
    }
}
