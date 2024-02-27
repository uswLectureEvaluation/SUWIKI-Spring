package usw.suwiki.global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class ETagConfig {

    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>(
            new ShallowEtagHeaderFilter()
        );

        filterFilterRegistrationBean.addUrlPatterns("/lecture/all");
        filterFilterRegistrationBean.setName("etagFilter");
        return new ShallowEtagHeaderFilter();
    }

}
