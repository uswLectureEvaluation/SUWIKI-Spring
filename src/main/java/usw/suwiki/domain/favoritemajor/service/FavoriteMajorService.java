package usw.suwiki.domain.favoritemajor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.favoritemajor.FavoriteMajor;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.repository.FavoriteMajorRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.exception.errortype.FavoriteMajorException;

import javax.transaction.Transactional;
import java.util.List;

import static usw.suwiki.global.exception.ExceptionType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class FavoriteMajorService {
    private final UserCRUDService userCRUDService;
    private final FavoriteMajorRepository favoriteMajorRepository;

    /**
     * userBusinessService 거쳐서 반환값 넘어 가는 중..
     * userBusinessService 가 너무 커서 분리 작업할 때 이리로 코드 옮겨도 괜찮을 듯
     */
    public void save(FavoriteSaveDto favoriteSaveDto, Long userIdx) {
        User loginUser = userCRUDService.loadUserByIdx(userIdx);
        String majorType = favoriteSaveDto.getMajorType();
        validateDuplicateFavoriteMajor(loginUser, majorType);
        FavoriteMajor favoriteMajor = FavoriteMajor.builder()
                .user(loginUser)
                .majorType(majorType)
                .build();
        favoriteMajorRepository.save(favoriteMajor);
    }

    private void validateDuplicateFavoriteMajor(User loginUser, String majorType) {
        boolean exists = favoriteMajorRepository.existsByUserIdAndMajorType(loginUser.getId(), majorType);
        if (!exists) return;

        throw new FavoriteMajorException(FAVORITE_MAJOR_DUPLICATE_REQUEST);
    }

    public List<String> findAllMajorTypeByUser(Long userIdx) {
        User loginUser = userCRUDService.loadUserByIdx(userIdx);

        List<FavoriteMajor> favoriteMajors = favoriteMajorRepository.findAllByUserId(loginUser.getId());
        return favoriteMajors.stream().map(FavoriteMajor::getMajorType).toList();
    }

    public void delete(Long userIdx, String majorType) {
        User loginUser = userCRUDService.loadUserByIdx(userIdx);

        FavoriteMajor favoriteMajor = favoriteMajorRepository.findByUserIdAndMajorType(loginUser.getId(), majorType)
                .orElseThrow(() -> new FavoriteMajorException(FAVORITE_MAJOR_NOT_FOUND));

        favoriteMajorRepository.delete(favoriteMajor);
    }

    public void deleteAllFromUserIdx(Long userIdx) {
        User loginUser = userCRUDService.loadUserByIdx(userIdx);
        List<FavoriteMajor> favoriteMajors = favoriteMajorRepository.findAllByUserId(loginUser.getId());
        favoriteMajorRepository.deleteAll(favoriteMajors);
    }
}
