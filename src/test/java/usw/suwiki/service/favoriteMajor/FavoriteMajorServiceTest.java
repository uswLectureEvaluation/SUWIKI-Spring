package usw.suwiki.service.favoriteMajor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import usw.suwiki.domain.favoritemajor.FavoriteMajor;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.repository.FavoriteMajorRepository;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.exception.errortype.FavoriteMajorException;
import usw.suwiki.template.favoriteMajor.FavoriteMajorTemplate;
import usw.suwiki.template.user.UserTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static usw.suwiki.global.exception.ExceptionType.FAVORITE_MAJOR_DUPLICATE_REQUEST;
import static usw.suwiki.global.exception.ExceptionType.FAVORITE_MAJOR_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
public class FavoriteMajorServiceTest {

    private static final User dummyUser = UserTemplate.createDummyUser();
    private static final FavoriteMajor dummyFavoriteMajor = FavoriteMajorTemplate.createDummyFavoriteMajor(dummyUser);
    private static final FavoriteMajor getDummyFavoriteMajor_business = FavoriteMajorTemplate.createDummyFavoriteMajor(2L, dummyUser, "경영");
    @InjectMocks
    FavoriteMajorService favoriteMajorService;
    @Mock
    UserCRUDService userCRUDService;
    @Mock
    FavoriteMajorRepository favoriteMajorRepository;

    @DisplayName("즐겨찾기 전공 저장 - 성공")
    @Test
    public void saveFavoriteMajor_success() throws Exception {
        //given
        given(userCRUDService.loadUserByIdx(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepository.existsByUserIdAndMajorType(dummyUser.getId(), dummyFavoriteMajor.getMajorType()))
                .willReturn(false);
        given(favoriteMajorRepository.save(any(FavoriteMajor.class))).willReturn(dummyFavoriteMajor);
        //when
        favoriteMajorService.save(new FavoriteSaveDto(dummyFavoriteMajor.getMajorType()), dummyUser.getId());

        //then
        verify(favoriteMajorRepository, times(1)).save(any());
    }

    @DisplayName("즐겨찾기 전공 저장 - 실패(이미 즐겨찾기 한 전공)")
    @Test
    public void saveFavoriteMajor_fail_for_duplicate_request() throws Exception {
        //given
        given(userCRUDService.loadUserByIdx(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepository.existsByUserIdAndMajorType(dummyUser.getId(), dummyFavoriteMajor.getMajorType()))
                .willReturn(true);
        //when
        assertThatThrownBy(() -> favoriteMajorService.save(new FavoriteSaveDto(dummyFavoriteMajor.getMajorType()), dummyUser.getId()))
                .isInstanceOf(FavoriteMajorException.class)
                .hasMessage(FAVORITE_MAJOR_DUPLICATE_REQUEST.getMessage())
        ;
    }

    @DisplayName("단일 즐겨찾기 전공 찾기 - 성공")
    @Test
    public void findSingleFavoriteMajorByUser_success() throws Exception {
        //given
        given(userCRUDService.loadUserByIdx(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepository.findAllByUserId(dummyUser.getId())).willReturn(List.of(dummyFavoriteMajor));

        //when
        List<String> response = favoriteMajorService.findAllMajorTypeByUser(dummyUser.getId());

        //then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0)).isEqualTo("컴퓨터SW");
    }

    @DisplayName("여러 즐겨찾기 전공 찾기 - 성공")
    @Test
    public void findMultiFavoriteMajorByUser_success() throws Exception {
        //given
        given(userCRUDService.loadUserByIdx(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepository.findAllByUserId(dummyUser.getId()))
                .willReturn(List.of(dummyFavoriteMajor, getDummyFavoriteMajor_business));

        //when
        List<String> response = favoriteMajorService.findAllMajorTypeByUser(dummyUser.getId());

        //then
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0)).isEqualTo("컴퓨터SW");
        assertThat(response.get(1)).isEqualTo("경영");
    }

    @DisplayName("단일 즐겨찾기 전공 삭제 - 성공")
    @Test
    public void deleteSingleFavoriteMajor_success() throws Exception {
        //given
        given(userCRUDService.loadUserByIdx(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepository.findByUserIdAndMajorType(dummyUser.getId(), dummyFavoriteMajor.getMajorType()))
                .willReturn(Optional.of(dummyFavoriteMajor));
        doNothing().when(favoriteMajorRepository).delete(dummyFavoriteMajor);

        //when
        favoriteMajorService.delete(dummyUser.getId(), dummyFavoriteMajor.getMajorType());

        //then
        verify(favoriteMajorRepository, times(1)).delete(dummyFavoriteMajor);
    }

    @DisplayName("단일 즐겨찾기 전공 삭제 - 실패(존재하지 않는 즐겨찾기 전공)")
    @Test
    public void deleteSingleFavoriteMajor_fail_not_exist() throws Exception {
        //given
        given(userCRUDService.loadUserByIdx(dummyUser.getId())).willReturn(dummyUser);
        given(favoriteMajorRepository.findByUserIdAndMajorType(dummyUser.getId(), "없음")).willReturn(Optional.empty());

        //when - then
        assertThatThrownBy(() -> favoriteMajorService.delete(dummyUser.getId(), "없음"))
                .isInstanceOf(FavoriteMajorException.class)
                .hasMessage(FAVORITE_MAJOR_NOT_FOUND.getMessage());
    }

    @DisplayName("여러 즐겨찾기 전공 삭제 - 성공")
    @Test
    public void deleteMultiFavoriteMajor_success() throws Exception {
        //given
        given(userCRUDService.loadUserByIdx(dummyUser.getId())).willReturn(dummyUser);
        List<FavoriteMajor> favoriteMajors = List.of(dummyFavoriteMajor, getDummyFavoriteMajor_business);
        given(favoriteMajorRepository.findAllByUserId(dummyUser.getId())).willReturn(favoriteMajors);
        doNothing().when(favoriteMajorRepository).deleteAll(favoriteMajors);

        //when
        favoriteMajorService.deleteAllFromUserIdx(dummyUser.getId());

        //then
        verify(favoriteMajorRepository, times(1)).deleteAll(favoriteMajors);
    }
}
