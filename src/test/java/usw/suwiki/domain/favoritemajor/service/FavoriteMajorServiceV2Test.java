package usw.suwiki.domain.favoritemajor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static usw.suwiki.global.exception.ExceptionType.FAVORITE_MAJOR_DUPLICATE_REQUEST;
import static usw.suwiki.global.exception.ExceptionType.FAVORITE_MAJOR_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import usw.suwiki.domain.favoritemajor.FavoriteMajor;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.fixture.FavoriteMajorFixture;
import usw.suwiki.domain.favoritemajor.repository.FavoriteMajorRepositoryV2;
import usw.suwiki.domain.user.fixture.UserFixture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.exception.errortype.FavoriteMajorException;
import usw.suwiki.global.jwt.JwtAgent;

@ExtendWith(MockitoExtension.class)
public class FavoriteMajorServiceV2Test {

    private static final User dummyUser = UserFixture.createDummyUser();
    private static final FavoriteMajor dummyFavoriteMajor = FavoriteMajorFixture.createDummyFavoriteMajor(dummyUser);
    private static final FavoriteMajor getDummyFavoriteMajor_business = FavoriteMajorFixture.createDummyFavoriteMajor(2L, dummyUser, "경영");
    private static final String AUTHORIZATION_TOKEN = "authorization";
    @InjectMocks
    FavoriteMajorServiceV2 favoriteMajorServiceV2;
    @Mock
    UserCRUDService userCRUDService;
    @Mock
    UserBusinessService userBusinessService;
    @Mock
    FavoriteMajorRepositoryV2 favoriteMajorRepositoryV2;
    @Mock
    JwtAgent jwtAgent;

    private void injectDummyUserId() {
        doNothing().when(userBusinessService).validateRestrictedUser(AUTHORIZATION_TOKEN);
        given(jwtAgent.getId(AUTHORIZATION_TOKEN)).willReturn(dummyUser.getId());
    }

    @DisplayName("즐겨찾기 전공 저장 - 성공")
    @Test
    public void saveFavoriteMajor_success() throws Exception {
        //given
        injectDummyUserId();
        given(userCRUDService.loadUserById(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepositoryV2.existsByUserIdAndMajorType(dummyUser.getId(), dummyFavoriteMajor.getMajorType()))
                .willReturn(false);
        given(favoriteMajorRepositoryV2.save(any(FavoriteMajor.class))).willReturn(dummyFavoriteMajor);

        //when
        favoriteMajorServiceV2.save(AUTHORIZATION_TOKEN, new FavoriteSaveDto("컴퓨터SW"));

        //then
        verify(favoriteMajorRepositoryV2, times(1)).save(any());
    }



    @DisplayName("즐겨찾기 전공 저장 - 실패(이미 즐겨찾기 한 전공)")
    @Test
    public void saveFavoriteMajor_fail_for_duplicate_request() throws Exception {
        //given
        injectDummyUserId();
        given(userCRUDService.loadUserById(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepositoryV2.existsByUserIdAndMajorType(dummyUser.getId(), dummyFavoriteMajor.getMajorType()))
                .willReturn(true);
        //when
        assertThatThrownBy(() -> favoriteMajorServiceV2.save(AUTHORIZATION_TOKEN, new FavoriteSaveDto("컴퓨터SW")))
                .isInstanceOf(FavoriteMajorException.class)
                .hasMessage(FAVORITE_MAJOR_DUPLICATE_REQUEST.getMessage())
        ;
    }

    @DisplayName("단일 즐겨찾기 전공 찾기 - 성공")
    @Test
    public void findSingleFavoriteMajorByUser_success() throws Exception {
        //given
        injectDummyUserId();
        given(userCRUDService.loadUserById(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepositoryV2.findAllByUserId(dummyUser.getId())).willReturn(List.of(dummyFavoriteMajor));

        //when
        List<String> response = favoriteMajorServiceV2.findAllMajorTypeByUser(AUTHORIZATION_TOKEN);

        //then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0)).isEqualTo("컴퓨터SW");
    }

    @DisplayName("여러 즐겨찾기 전공 찾기 - 성공")
    @Test
    public void findMultiFavoriteMajorByUser_success() throws Exception {
        //given
        injectDummyUserId();
        given(userCRUDService.loadUserById(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepositoryV2.findAllByUserId(dummyUser.getId()))
                .willReturn(List.of(dummyFavoriteMajor, getDummyFavoriteMajor_business));

        //when
        List<String> response = favoriteMajorServiceV2.findAllMajorTypeByUser(AUTHORIZATION_TOKEN);

        //then
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0)).isEqualTo("컴퓨터SW");
        assertThat(response.get(1)).isEqualTo("경영");
    }

    @DisplayName("단일 즐겨찾기 전공 삭제 - 성공")
    @Test
    public void deleteSingleFavoriteMajor_success() throws Exception {
        //given
        injectDummyUserId();
        given(userCRUDService.loadUserById(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepositoryV2.findByUserIdAndMajorType(dummyUser.getId(), dummyFavoriteMajor.getMajorType()))
                .willReturn(Optional.of(dummyFavoriteMajor));
        doNothing().when(favoriteMajorRepositoryV2).delete(dummyFavoriteMajor);

        //when
        favoriteMajorServiceV2.delete(AUTHORIZATION_TOKEN, dummyFavoriteMajor.getMajorType());

        //then
        verify(favoriteMajorRepositoryV2, times(1)).delete(dummyFavoriteMajor);
    }

    @DisplayName("단일 즐겨찾기 전공 삭제 - 실패(존재하지 않는 즐겨찾기 전공)")
    @Test
    public void deleteSingleFavoriteMajor_fail_not_exist() throws Exception {
        //given
        injectDummyUserId();
        given(userCRUDService.loadUserById(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepositoryV2.findByUserIdAndMajorType(dummyUser.getId(), "없음")).willReturn(Optional.empty());

        //when - then
        assertThatThrownBy(() -> favoriteMajorServiceV2.delete(AUTHORIZATION_TOKEN, "없음"))
                .isInstanceOf(FavoriteMajorException.class)
                .hasMessage(FAVORITE_MAJOR_NOT_FOUND.getMessage());
    }

    @DisplayName("여러 즐겨찾기 전공 삭제 - 성공")
    @Test
    public void deleteMultiFavoriteMajor_success() throws Exception {
        //given
        given(userCRUDService.loadUserById(dummyUser.getId())).willReturn(dummyUser);
        List<FavoriteMajor> favoriteMajors = List.of(dummyFavoriteMajor, getDummyFavoriteMajor_business);
        given(favoriteMajorRepositoryV2.findAllByUserId(dummyUser.getId())).willReturn(favoriteMajors);
        doNothing().when(favoriteMajorRepositoryV2).deleteAll(favoriteMajors);

        //when
        favoriteMajorServiceV2.deleteAllFromUserIdx(dummyUser.getId());

        //then
        verify(favoriteMajorRepositoryV2, times(1)).deleteAll(favoriteMajors);
    }
}
