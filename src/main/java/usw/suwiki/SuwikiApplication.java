package usw.suwiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import usw.suwiki.global.properties.ServerProperties;

@EnableScheduling
@EnableConfigurationProperties(value = {ServerProperties.class})
@SpringBootApplication
public class SuwikiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SuwikiApplication.class, args);
    }
}

