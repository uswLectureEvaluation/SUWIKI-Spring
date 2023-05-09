package usw.suwiki.domain.favoritemajor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.favoritemajor.FavoriteMajor;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.repository.FavoriteMajorRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class FavoriteMajorService {
    private final FavoriteMajorRepository favoriteMajorRepository;
    private final UserRepository userRepository;

    public void save(FavoriteSaveDto dto, Long userIdx) {
        FavoriteMajor favorite = new FavoriteMajor(dto.getMajorType());
        Optional<User> user = userRepository.findById(userIdx);
        favorite.setUser(user.get());
        favoriteMajorRepository.save(favorite);
    }

    public List<String> findMajorTypeByUser(Long userIdx) {
        List<String> list = favoriteMajorRepository.findOnlyMajorTypeByUser(userIdx);
        return list;
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
