package usw.suwiki.controller.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.dto.userAdmin.UserAdminDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.jwt.JwtTokenResolver;
import usw.suwiki.jwt.JwtTokenValidator;
import usw.suwiki.service.userAdmin.UserAdminService;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserAdminController {

    private final UserAdminService userAdminService;
    private final JwtTokenResolver jwtTokenResolver;
    private final JwtTokenValidator jwtTokenValidator;

    @PostMapping("ban")
    public HashMap<String, Boolean> blackList(@Valid @RequestHeader String Authorization, UserAdminDto.BannedTargetForm bannedTargetForm) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);
        
        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
//        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) throw new AccountException(ErrorType.USER_RESTRICTED);


        HashMap<String, Boolean> returnJson = new HashMap<>();

        userAdminService.banPost(bannedTargetForm);
        userAdminService.banUser(bannedTargetForm);

        returnJson.put("Success", true);
        return returnJson;
    }
}
