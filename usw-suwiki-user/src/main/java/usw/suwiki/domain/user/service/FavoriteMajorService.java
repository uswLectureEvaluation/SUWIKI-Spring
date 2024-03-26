package usw.suwiki.domain.user.service;

import java.util.List;

public interface FavoriteMajorService {
  void clear(Long userId);

  void delete(Long userId, String type);

  void save(Long userId, String majorType);

  List<String> findMajorTypeByUser(Long userId);
}
