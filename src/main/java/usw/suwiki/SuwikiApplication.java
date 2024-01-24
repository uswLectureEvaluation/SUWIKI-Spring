package usw.suwiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import usw.suwiki.global.properties.ServerProperties;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableCaching
@EnableConfigurationProperties(value = {ServerProperties.class})
public class SuwikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuwikiApplication.class, args);
    }
}

