package usw.suwiki.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import usw.suwiki.domain.emailToken.ConfirmationToken;

@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public ConfirmationToken confirmationToken() {
        return new ConfirmationToken();
    }

}
