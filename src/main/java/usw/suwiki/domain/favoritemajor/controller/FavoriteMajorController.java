package usw.suwiki.domain.favoritemajor.controller;

import static org.springframework.http.HttpStatus.OK;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorServiceV2;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;

@RestController
@RequestMapping("/v2/favorite-major")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FavoriteMajorController {

    private final FavoriteMajorServiceV2 favoriteMajorServiceV2;

    @ResponseStatus(OK)
    @Operation(summary = "전공 즐겨찾기 등록")
    @ApiLogger(option = "user")
    @PostMapping
    public String create(
        @RequestHeader String Authorization,
        @RequestBody FavoriteSaveDto favoriteSaveDto
    ) {
        favoriteMajorServiceV2.save(Authorization, favoriteSaveDto);
        return "success";
    }

    @ResponseStatus(OK)
    @Operation(summary = "전공 즐겨찾기 삭제")
    @ApiLogger(option = "user")
    @DeleteMapping
    public String delete(
        @RequestHeader String Authorization,
        @RequestParam String majorType
    ) {
        favoriteMajorServiceV2.delete(Authorization, majorType);
        return "success";
    }

    @ResponseStatus(OK)
    @Operation(summary = "전공 즐겨찾기 불러오기")
    @ApiLogger(option = "user")
    @GetMapping
    public ResponseForm retrieve(@RequestHeader String Authorization) {
        return new ResponseForm(favoriteMajorServiceV2.findAllMajorTypeByUser(Authorization));
    }

}
