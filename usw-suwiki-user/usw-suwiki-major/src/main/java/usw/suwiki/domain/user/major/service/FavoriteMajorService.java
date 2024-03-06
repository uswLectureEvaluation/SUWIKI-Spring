package usw.suwiki.domain.user.major.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.major.FavoriteMajor;
import usw.suwiki.domain.user.major.FavoriteMajorRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteMajorService {
    private final FavoriteMajorRepository favoriteMajorRepository;
    private final UserRepository userRepository;

    public void save(FavoriteSaveDto dto, Long userIdx) {
        FavoriteMajor favorite = new FavoriteMajor(dto.getMajorType());
        Optional<User> user = userRepository.findById(userIdx);
        favorite.setUser(user.get());
        favoriteMajorRepository.save(favorite);
    }

    @Transactional(readOnly = true)
    public List<String> findMajorTypeByUser(Long userIdx) {
        return favoriteMajorRepository.findOnlyMajorTypeByUser(userIdx);
    }

    public void delete(Long userIdx, String majorType) {
        FavoriteMajor favorite = favoriteMajorRepository.findByUserAndMajorType(userIdx, majorType);
        favoriteMajorRepository.delete(favorite);
    }

    public void deleteFromUserIdx(Long userIdx) {
        List<FavoriteMajor> list = favoriteMajorRepository.findAllByUser(userIdx);
        for (FavoriteMajor favorite : list) {
            favoriteMajorRepository.delete(favorite);
        }
    }
}
