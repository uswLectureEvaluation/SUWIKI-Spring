package usw.suwiki.domain.user.major.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.FavoriteMajorException;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.major.FavoriteMajor;
import usw.suwiki.domain.user.major.FavoriteMajorRepositoryV2;
import usw.suwiki.domain.user.service.FavoriteMajorServiceV2;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.domain.user.service.UserCRUDService;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteMajorServiceV2Impl implements FavoriteMajorServiceV2 {
    private final UserCRUDService userCRUDService;
    private final UserBusinessService userBusinessService;
    private final FavoriteMajorRepositoryV2 favoriteMajorRepositoryV2;

    private final JwtAgent jwtAgent;

    @Override
    public void save(String authorization, String majorType) {
        userBusinessService.validateRestrictedUser(authorization);
        User loginUser = userCRUDService.loadUserById(jwtAgent.getId(authorization));

        validateDuplicateFavoriteMajor(loginUser, majorType);

        favoriteMajorRepositoryV2.save(FavoriteMajor.builder()
          .user(loginUser)
          .majorType(majorType)
          .build());
    }

    private void validateDuplicateFavoriteMajor(User loginUser, String majorType) {
        if (favoriteMajorRepositoryV2.existsByUserIdAndMajorType(loginUser.getId(), majorType)) {
            throw new FavoriteMajorException(ExceptionType.FAVORITE_MAJOR_DUPLICATE_REQUEST);
        }
    }

    @Override
    public List<String> findAllMajorTypeByUser(String authorization) {
        userBusinessService.validateRestrictedUser(authorization);
        User loginUser = userCRUDService.loadUserById(jwtAgent.getId(authorization));

        List<FavoriteMajor> favoriteMajors = favoriteMajorRepositoryV2.findAllByUserId(loginUser.getId());
        return favoriteMajors.stream().map(FavoriteMajor::getMajorType).toList();
    }

    @Override
    public void delete(String authorization, String majorType) {
        userBusinessService.validateRestrictedUser(authorization);
        User loginUser = userCRUDService.loadUserById(jwtAgent.getId(authorization));

        FavoriteMajor favoriteMajor = favoriteMajorRepositoryV2.findByUserIdAndMajorType(loginUser.getId(), majorType)
          .orElseThrow(() -> new FavoriteMajorException(ExceptionType.FAVORITE_MAJOR_NOT_FOUND));

        favoriteMajorRepositoryV2.delete(favoriteMajor);
    }

    @Override
    public void deleteAllFromUserIdx(Long userIdx) {
        User loginUser = userCRUDService.loadUserById(userIdx);
        List<FavoriteMajor> favoriteMajors = favoriteMajorRepositoryV2.findAllByUserId(loginUser.getId());
        favoriteMajorRepositoryV2.deleteAll(favoriteMajors);
    }
}
