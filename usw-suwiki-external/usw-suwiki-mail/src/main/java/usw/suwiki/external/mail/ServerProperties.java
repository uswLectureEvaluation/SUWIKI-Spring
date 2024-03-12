package usw.suwiki.external.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "server")
@RequiredArgsConstructor
public class ServerProperties {
    private static final String CONFIRMATION_TOKEN_URL = "/v2/confirmation-token/verify/?token=";

    private final String domain;
    private final int port;

    String redirectUrl(String token) {
        return domain + CONFIRMATION_TOKEN_URL + token;
    }
}
