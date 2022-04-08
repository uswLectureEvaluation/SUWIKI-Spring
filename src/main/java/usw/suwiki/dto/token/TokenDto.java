package usw.suwiki.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.user.Role;

public class TokenDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDto {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    @AllArgsConstructor
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;
    }
}
