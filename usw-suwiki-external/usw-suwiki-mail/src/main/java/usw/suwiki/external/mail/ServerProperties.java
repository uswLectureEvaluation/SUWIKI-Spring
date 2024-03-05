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
    private final String domain;
    private final int port;
}
