package usw.suwiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@EnableConfigurationProperties(value = {ServerProperties.class})
@SpringBootApplication
public class SuwikiClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(SuwikiClientApplication.class, args);
  }

  @GetMapping("/health")
  @ResponseStatus(OK)
  public String check() {
    return "✅";
  }
}
