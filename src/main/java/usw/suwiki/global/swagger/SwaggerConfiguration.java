package usw.suwiki.global.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(new Info()
                        .title("SUWIKI API")
                        .description("Description By Richard With Diger"));
    }

}