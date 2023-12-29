package usw.suwiki.service.timetable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import usw.suwiki.domain.timetable.dto.request.CreateTimetableRequest;
import usw.suwiki.domain.timetable.dto.response.CreateTimetableResponse;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.repository.TimetableRepository;
import usw.suwiki.domain.timetable.service.TimetableService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.template.timetable.TimetableTemplate;
import usw.suwiki.template.user.UserTemplate;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TimetableServiceTest {
    @InjectMocks
    TimetableService timetableService;

    @Mock
    UserCRUDService userCRUDService;

    @Mock
    TimetableRepository timetableRepository;

    private User user;
    private Timetable timetable;

    private final static Long SPYING_TIMETABLE_ID = 1L;
    private final static Long RANDOM_ID = 193486L;


    @BeforeEach
    public void setUp() {
        this.user = UserTemplate.createDummyUser();
        this.timetable = spy(TimetableTemplate.createFirstDummy(user));
    }

    @Test
    @DisplayName("시간표 생성")
    public void CREATE_TIMETABLE() {
        // given
        final CreateTimetableRequest request = CreateTimetableRequest.builder()
                .year(timetable.getYear())
                .semester(timetable.getSemester().getValue())
                .name(timetable.getName())
                .build();

        given(userCRUDService.loadUserById(anyLong())).willReturn(user);
        given(timetableRepository.save(any(Timetable.class))).willReturn(timetable);
        when(timetable.getId()).thenReturn(SPYING_TIMETABLE_ID);

        // when
        CreateTimetableResponse response = timetableService.createTimetable(request, RANDOM_ID);

        // then
        verify(userCRUDService).loadUserById(anyLong());
        verify(timetableRepository).save(any(Timetable.class));
        assertThat(response.getId()).isEqualTo(SPYING_TIMETABLE_ID);
        assertThat(response.getYear()).isEqualTo(request.getYear());
    }

    // 시간표 수정 성공
    // 시간표 수정 실패 - 존재하지 않는 시간표
    // 시간표 수정 실패 - 시간표 수정의 주체는 작성자여야 한다.

    // 시간표 삭제 성공
    // 시간표 삭제 실패 - 존재하지 않는 시간표
    // 시간표 삭제 실패 - 시간표 삭제의 주체는 작성자여야 한다.

    // 시간표 리스트 조회 성공

    // 시간표 상세 조회 성공
    // 시간표 상세 조회 실패 - 존재하지 않는 시간표

    // 시간표 셀 생성 성공
    // 시간표 셀 생성 실패 - 존재하지 않는 시간표
    // 시간표 셀 생성 실패 - 유효하지 않은 색상
    // 시간표 셀 생성 실패 - (요일, 교시)는 중복되어선 안 된다.

    // 시간표 셀 수정 성공
    // 시간표 셀 수정 실패 - 존재하지 않는 시간표 셀
    // 시간표 셀 수정 실패 - 유효하지 않은 색상
    // 시간표 셀 수정 실패 - 시간표 셀 수정의 주체는 작성자여야 한다.
    // 시간표 셀 수정 실패 - (요일, 교시)는 중복되어선 안 된다.

    // 시간표 셀 삭제 성공
    // 시간표 셀 삭제 실패 - 존재하지 않는 시간표 셀
    // 시간표 셀 삭제 실패 - 시간표 셀 삭제의 주체는 작성자여야 한다.
}
