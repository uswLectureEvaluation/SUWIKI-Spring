package usw.suwiki.domain.lecture.major.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.FavoriteMajorException;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.domain.lecture.major.FavoriteMajor;
import usw.suwiki.domain.lecture.major.FavoriteMajorRepositoryV2;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteMajorServiceV2 {
  private final FavoriteMajorRepositoryV2 favoriteMajorRepositoryV2;

  private final TokenAgent tokenAgent;

  public void save(String authorization, String majorType) {
    validateRestrictedUser(authorization);
    Long userId = tokenAgent.getId(authorization);

    validateDuplicateFavoriteMajor(userId, majorType);

    favoriteMajorRepositoryV2.save(new FavoriteMajor(userId, majorType));
  }

  private void validateDuplicateFavoriteMajor(Long userId, String majorType) {
    if (favoriteMajorRepositoryV2.existsByUserIdAndMajorType(userId, majorType)) {
      throw new FavoriteMajorException(ExceptionType.FAVORITE_MAJOR_DUPLICATE_REQUEST);
    }
  }

  public List<String> findAllMajorTypeByUser(String authorization) {
    validateRestrictedUser(authorization);
    Long userId = tokenAgent.getId(authorization);

    List<FavoriteMajor> favoriteMajors = favoriteMajorRepositoryV2.findAllByUserId(userId);
    return favoriteMajors.stream().map(FavoriteMajor::getMajorType).toList();
  }

  public void delete(String authorization, String majorType) {
    validateRestrictedUser(authorization);
    Long userId = tokenAgent.getId(authorization);

    FavoriteMajor favoriteMajor = favoriteMajorRepositoryV2.findByUserIdAndMajorType(userId, majorType)
      .orElseThrow(() -> new FavoriteMajorException(ExceptionType.FAVORITE_MAJOR_NOT_FOUND));

    favoriteMajorRepositoryV2.delete(favoriteMajor);
  }

  public void deleteAllFromUserIdx(Long userId) {
    List<FavoriteMajor> favoriteMajors = favoriteMajorRepositoryV2.findAllByUserId(userId);
    favoriteMajorRepositoryV2.deleteAll(favoriteMajors);
  }

  private void validateRestrictedUser(String authorization) {
    if (tokenAgent.getUserIsRestricted(authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }
  }
}
