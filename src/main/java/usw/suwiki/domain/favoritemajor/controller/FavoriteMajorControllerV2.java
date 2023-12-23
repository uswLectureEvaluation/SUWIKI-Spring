package usw.suwiki.domain.favoritemajor.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorServiceV2;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/favorite-major")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FavoriteMajorControllerV2 {

    private final FavoriteMajorServiceV2 favoriteMajorServiceV2;

    // 전공 즐겨찾기 등록하기
    @ResponseStatus(OK)
    @Operation(summary = "전공 즐겨찾기 등록")
    @ApiLogger(option = "user")
    @PostMapping
    public String saveFavoriteMajor(
            @RequestHeader String Authorization,
            @RequestBody FavoriteSaveDto favoriteSaveDto
    ) {
        favoriteMajorServiceV2.save(Authorization, favoriteSaveDto);
        return "success";
    }

    // 전공 즐겨찾기 삭제하기
    @ResponseStatus(OK)
    @Operation(summary = "전공 즐겨찾기 삭제")
    @ApiLogger(option = "user")
    @DeleteMapping
    public String deleteFavoriteMajor(
            @RequestHeader String Authorization,
            @RequestParam String majorType) {
        favoriteMajorServiceV2.delete(Authorization, majorType);
        return "success";
    }

    // 전공 즐겨찾기 불러오기
    @ResponseStatus(OK)
    @Operation(summary = "전공 즐겨찾기 불러오기")
    @ApiLogger(option = "user")
    @GetMapping
    public ResponseForm loadFavoriteMajor(@RequestHeader String Authorization) {
        return new ResponseForm(favoriteMajorServiceV2.findAllMajorTypeByUser(Authorization));
    }

}
