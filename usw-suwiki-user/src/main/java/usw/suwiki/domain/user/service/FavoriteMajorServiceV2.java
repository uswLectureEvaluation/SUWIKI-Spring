package usw.suwiki.domain.user.service;

import java.util.List;

public interface FavoriteMajorServiceV2 {
  void save(String authorization, String majorType);

  List<String> findAllMajorTypeByUser(String authorization);

  void delete(String authorization, String majorType);

  void deleteAllFromUserIdx(Long userIdx);
}
