package usw.suwiki.domain.favoritemajor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/favorite-major")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FavoriteMajorControllerV2 {

    private final UserBusinessService userBusinessService;

    // 전공 즐겨찾기 등록하기
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping
    public String saveFavoriteMajor(
            @RequestHeader String Authorization,
            @RequestBody FavoriteSaveDto favoriteSaveDto
    ) {
        userBusinessService.executeFavoriteMajorSave(Authorization, favoriteSaveDto);
        return "success";
    }

    // 전공 즐겨찾기 삭제하기
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @DeleteMapping
    public String deleteFavoriteMajor(
            @RequestHeader String Authorization,
            @RequestParam String majorType) {
        userBusinessService.executeFavoriteMajorDelete(Authorization, majorType);
        return "success";
    }

    // 전공 즐겨찾기 불러오기
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping
    public ResponseForm loadFavoriteMajor(@RequestHeader String Authorization) {
        return userBusinessService.executeFavoriteMajorLoad(Authorization);
    }

}
