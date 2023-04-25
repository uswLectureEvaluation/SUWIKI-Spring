package usw.suwiki.domain.user.user.service;

import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFavoriteMajorService {

    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;
    private final FavoriteMajorService favoriteMajorService;

    public void executeSave(String Authorization, FavoriteSaveDto favoriteSaveDto) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtResolver.getId(Authorization);
        favoriteMajorService.save(favoriteSaveDto, userIdx);

    }

    public void executeDelete(String Authorization, String majorType) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtResolver.getId(Authorization);
        favoriteMajorService.delete(userIdx, majorType);
    }

    public ResponseForm executeLoad(String Authorization) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtResolver.getId(Authorization);
        List<String> list = favoriteMajorService.findMajorTypeByUser(userIdx);
        return new ResponseForm(list);
    }
}