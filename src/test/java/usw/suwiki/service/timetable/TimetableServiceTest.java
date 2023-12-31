package usw.suwiki.service.timetable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import usw.suwiki.domain.timetable.dto.request.UpdateTimetableRequest;
import usw.suwiki.domain.timetable.dto.response.SimpleTimetableResponse;
import usw.suwiki.domain.timetable.dto.response.TimetableResponse;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.entity.TimetableDay;
import usw.suwiki.domain.timetable.repository.TimetableRepository;
import usw.suwiki.domain.timetable.service.TimetableService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.TimetableException;
import usw.suwiki.template.timetable.TimetableTemplate;
import usw.suwiki.template.timetablecell.TimetableCellTemplate;
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
    private User otherUser;
    private Timetable timetable;
    private TimetableCell timetableCellA;
    private TimetableCell timetableCellB;

    private final static Long SPYING_TIMETABLE_ID = 1L;
    private final static Long SPYING_USER_ID = 100L;
    public static final Long RANDOM_ID = 2345L;
    private final static Long RANDOM_ID_A = 193486L;
    private final static Long RANDOM_ID_B = 98345L;


    @BeforeEach
    public void setUp() {
        this.user = spy(UserTemplate.createDummyUser());
        this.otherUser = spy(UserTemplate.createSecondDummyUser());
        this.timetable = spy(TimetableTemplate.createFirstDummy(user));
        this.timetableCellA = spy(TimetableCellTemplate.createFirstDummy(timetable));
        this.timetableCellB = spy(TimetableCellTemplate.createSecondDummy(timetable));
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
        SimpleTimetableResponse response = timetableService.createTimetable(request, RANDOM_ID);

        // then
        verify(userCRUDService).loadUserById(anyLong());
        verify(timetableRepository).save(any(Timetable.class));
        assertThat(response.getId()).isEqualTo(SPYING_TIMETABLE_ID);
        assertThat(response.getYear()).isEqualTo(request.getYear());
    }

    @Test
    @DisplayName("시간표 수정")
    public void UPDATE_TIMETABLE() {
        // given
        final UpdateTimetableRequest request = UpdateTimetableRequest.builder()
                .year(timetable.getYear())
                .semester(timetable.getSemester().getValue())
                .name("변경된 시간표")
                .build();

        given(userCRUDService.loadUserById(anyLong())).willReturn(user);
        given(timetableRepository.findById(anyLong())).willReturn(Optional.of(timetable));
        when(timetable.getId()).thenReturn(SPYING_TIMETABLE_ID);

        // when
        SimpleTimetableResponse response = timetableService.updateTimetable(request, RANDOM_ID, RANDOM_ID);

        // then
        verify(userCRUDService).loadUserById(anyLong());
        verify(timetableRepository).findById(anyLong());
        assertThat(response.getId()).isEqualTo(SPYING_TIMETABLE_ID);
        assertThat(response.getName()).isEqualTo(request.getName());    // 응답값에 수정된 내용이 반영되어야 한다.
    }

    @Test
    @DisplayName("시간표 수정 실패 - DB에 존재하는 시간표여야 한다.")
    public void UPDATE_TIMETABLE_FAIL_NOT_FOUND_TIMETABLE() {
        // given
        final UpdateTimetableRequest request = UpdateTimetableRequest.builder()
                .year(timetable.getYear())
                .semester(timetable.getSemester().getValue())
                .name("변경된 시간표")
                .build();

        given(userCRUDService.loadUserById(anyLong())).willReturn(user);
        given(timetableRepository.findById(anyLong())).willReturn(Optional.empty());    // 존재하지 않는 시간표 가정

        // when & then
        assertThatThrownBy(() -> timetableService.updateTimetable(request, RANDOM_ID, RANDOM_ID))
                .isExactlyInstanceOf(TimetableException.class)
                .hasMessage(ExceptionType.TIMETABLE_NOT_FOUND.getMessage());
        verify(userCRUDService).loadUserById(anyLong());
        verify(timetableRepository).findById(anyLong());
    }

    @Test
    @DisplayName("시간표 수정 실패 - 시간표 수정의 주체는 작성자여야 한다.")
    public void UPDATE_TIMETABLE_FAIL_NOT_AUTHOR() {
        // given
        final UpdateTimetableRequest request = UpdateTimetableRequest.builder()
                .year(timetable.getYear())
                .semester(timetable.getSemester().getValue())
                .name("변경된 시간표")
                .build();

        given(userCRUDService.loadUserById(anyLong())).willReturn(otherUser);
        given(timetableRepository.findById(anyLong())).willReturn(Optional.of(timetable));
        when(user.getId()).thenReturn(RANDOM_ID_A);
        when(otherUser.getId()).thenReturn(RANDOM_ID_B);    // 다른 유처가 요청한 상황을 가정. User id 비교 메서드

        // when & then
        assertThatThrownBy(() -> timetableService.updateTimetable(request, RANDOM_ID, RANDOM_ID))
                .isExactlyInstanceOf(TimetableException.class)
                .hasMessage(ExceptionType.TIMETABLE_NOT_AN_AUTHOR.getMessage());
        verify(userCRUDService).loadUserById(anyLong());
        verify(timetableRepository).findById(anyLong());
    }

    @Test
    @DisplayName("시간표 삭제")
    public void DELETE_TIMETABLE() {
        given(userCRUDService.loadUserById(anyLong())).willReturn(user);
        given(timetableRepository.findById(anyLong())).willReturn(Optional.of(timetable));
        when(user.getId()).thenReturn(SPYING_USER_ID);

        // when & then
        assertThatNoException().isThrownBy(() -> timetableService.deleteTimetable(RANDOM_ID, SPYING_USER_ID));
    }

    @Test
    @DisplayName("시간표 삭제 실패 - DB에 존재하는 시간표여야 한다.")
    public void DELETE_TIMETABLE_FAIL_NOT_FOUND_TIMETABLE() {
        given(userCRUDService.loadUserById(anyLong())).willReturn(user);
        given(timetableRepository.findById(anyLong())).willReturn(Optional.empty());    // 존재하지 않는 시간표 가정

        // when & then
        assertThatThrownBy(() -> timetableService.deleteTimetable(RANDOM_ID, RANDOM_ID))
                .isExactlyInstanceOf(TimetableException.class)
                .hasMessage(ExceptionType.TIMETABLE_NOT_FOUND.getMessage());
        verify(userCRUDService).loadUserById(anyLong());
        verify(timetableRepository).findById(anyLong());
    }

    @Test
    @DisplayName("시간표 삭제 실패 - 시간표 삭제의 주체는 작성자여야 한다.")
    public void DELETE_TIMETABLE_FAIL_NOT_AUTHOR() {
        // given
        given(userCRUDService.loadUserById(anyLong())).willReturn(otherUser);
        given(timetableRepository.findById(anyLong())).willReturn(Optional.of(timetable));
        when(user.getId()).thenReturn(RANDOM_ID_A);
        when(otherUser.getId()).thenReturn(RANDOM_ID_B);    // 다른 유처가 요청한 상황을 가정. User id 비교 메서드

        // when & then
        assertThatThrownBy(() -> timetableService.deleteTimetable(RANDOM_ID, RANDOM_ID))
                .isExactlyInstanceOf(TimetableException.class)
                .hasMessage(ExceptionType.TIMETABLE_NOT_AN_AUTHOR.getMessage());
        verify(userCRUDService).loadUserById(anyLong());
        verify(timetableRepository).findById(anyLong());
    }

    // 시간표 리스트 조회 성공
    @Test
    @DisplayName("시간표 리스트 조회 성공")
    public void SELECT_TIMETABLE_LIST() {
        // given
        given(timetableRepository.findAllByUserId(anyLong())).willReturn(List.of(timetable));
        when(timetable.getId()).thenReturn(RANDOM_ID);

        // when
        List<SimpleTimetableResponse> response = timetableService.getAllTimetableList(RANDOM_ID);

        // then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getId()).isEqualTo(timetable.getId());
        verify(timetableRepository).findAllByUserId(anyLong());
    }

    // 시간표 상세 조회 성공
    @Test
    @DisplayName("시간표 상세 조회 성공")
    public void SELECT_TIMETABLE_WITH_CELL_LIST() {
        // given
        given(timetableRepository.findById(anyLong())).willReturn(Optional.of(timetable));
        when(timetable.getId()).thenReturn(RANDOM_ID);
        when(timetable.getCellList()).thenReturn(List.of(timetableCellA, timetableCellB));
        when(timetableCellA.getId()).thenReturn(RANDOM_ID_A);
        when(timetableCellB.getId()).thenReturn(RANDOM_ID_B);

        // when
        TimetableResponse response = timetableService.getTimetable(RANDOM_ID);

        // then
        assertThat(response.getId()).isEqualTo(timetable.getId());
        assertThat(response.getName()).isEqualTo(timetable.getName());
        assertThat(response.getCellList().size()).isEqualTo(2);
        assertThat(response.getCellList().get(0).getLocation()).isEqualTo(timetableCellA.getSchedule().getLocation());
        assertThat(response.getCellList().get(1).getLocation()).isEqualTo(timetableCellB.getSchedule().getLocation());
        verify(timetableRepository).findById(anyLong());
    }

    @Test
    @DisplayName("시간표 상세 조회 실패 - 존재하지 않는 시간표")
    public void SELECT_TIMETABLE_WITH_CELL_LIST_FAIL_NOT_FOUND_TIMETABLE() {
        // given
        given(timetableRepository.findById(anyLong())).willReturn(Optional.empty());    // 존재하지 않음

        // when & then
        assertThatThrownBy(() -> timetableService.getTimetable(RANDOM_ID))
                .isExactlyInstanceOf(TimetableException.class)
                .hasMessage(ExceptionType.TIMETABLE_NOT_FOUND.getMessage());
        verify(timetableRepository).findById(anyLong());
    }

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
