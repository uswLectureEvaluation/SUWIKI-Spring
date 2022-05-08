package usw.suwiki.service.favorite_major;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.favorite_major.FavoriteMajor;
import usw.suwiki.domain.user.User;
import usw.suwiki.dto.favorite_major.FavoriteSaveDto;
import usw.suwiki.repository.favorite_major.JpaFavoriteMajorRepository;
import usw.suwiki.repository.user.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class FavoriteMajorService {
    private final JpaFavoriteMajorRepository jpaFavoriteMajorRepository;
    private final UserRepository userRepository;

    public void save(FavoriteSaveDto dto, Long userIdx){
        FavoriteMajor favorite = new FavoriteMajor(dto.getMajorType());
        Optional<User> user = userRepository.findById(userIdx);
        favorite.setUser(user.get());
        jpaFavoriteMajorRepository.save(favorite);
    }

    public void delete(Long userIdx, String majorType){
        FavoriteMajor favorite = jpaFavoriteMajorRepository.findByUserAndMajorType(userIdx, majorType);
        jpaFavoriteMajorRepository.delete(favorite);
    }

    public void deleteAllByUser(Long userIdx){
        List<FavoriteMajor> list = jpaFavoriteMajorRepository.findAllByUser(userIdx);
        for (FavoriteMajor favorite : list) {
            jpaFavoriteMajorRepository.delete(favorite);
        }
    }

}
