package usw.suwiki.domain.lecture.major.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.lecture.major.FavoriteMajor;
import usw.suwiki.domain.lecture.major.FavoriteMajorRepository;
import usw.suwiki.domain.user.service.FavoriteMajorService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
class FavoriteMajorServiceImpl implements FavoriteMajorService {
  private final FavoriteMajorRepository favoriteMajorRepository;

  @Override
  public void save(Long userId, String majorType) {
    favoriteMajorRepository.save(new FavoriteMajor(userId, majorType));
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> findMajorTypeByUser(Long userIdx) {
    return favoriteMajorRepository.findOnlyMajorTypeByUser(userIdx);
  }

  @Override
  public void delete(Long userIdx, String majorType) {
    FavoriteMajor favorite = favoriteMajorRepository.findByUserAndMajorType(userIdx, majorType);
    favoriteMajorRepository.delete(favorite);
  }

  @Override
  public void clear(Long userIdx) {
    List<FavoriteMajor> list = favoriteMajorRepository.findAllByUser(userIdx);
    for (FavoriteMajor favorite : list) {
      favoriteMajorRepository.delete(favorite);
    }
  }
}
