package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.global.ToJsonArray;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import java.util.List;

import static usw.suwiki.global.exception.ErrorType.BAD_REQUEST;
import static usw.suwiki.global.exception.ErrorType.USER_RESTRICTED;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFavoriteMajorService {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final FavoriteMajorService favoriteMajorService;

    public void executeSave(String Authorization, FavoriteSaveDto favoriteSaveDto) {
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization))
                throw new AccountException(USER_RESTRICTED);
            Long userIdx = jwtTokenResolver.getId(Authorization);
            favoriteMajorService.save(favoriteSaveDto, userIdx);
        }
    }

    public void executeDelete(String Authorization, String majorType) {
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization))
                throw new AccountException(USER_RESTRICTED);
            Long userIdx = jwtTokenResolver.getId(Authorization);
            favoriteMajorService.delete(userIdx, majorType);
        }
    }

    public ToJsonArray executeLoad(String Authorization) {
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization))
                throw new AccountException(USER_RESTRICTED);
            Long userIdx = jwtTokenResolver.getId(Authorization);
            List<String> list = favoriteMajorService.findMajorTypeByUser(userIdx);
            return new ToJsonArray(list);
        }
        throw new AccountException(BAD_REQUEST);
    }
}