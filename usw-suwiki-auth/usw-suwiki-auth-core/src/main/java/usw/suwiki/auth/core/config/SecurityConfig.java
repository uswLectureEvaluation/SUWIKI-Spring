package usw.suwiki.auth.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PERMIT_URL_ARRAY = {
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/swagger-ui/**",
        "/v3/api-docs",
        "/swagger-ui.html",
        "/webjars/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .headers().frameOptions().disable()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
            .authorizeRequests()
            .antMatchers(PERMIT_URL_ARRAY).permitAll()
            .antMatchers("/user/**").permitAll()
            .antMatchers("/evaluate-posts/**").permitAll()
            .antMatchers("/exam-posts/**").permitAll()
            .antMatchers("/notice/**").permitAll()
            .antMatchers("/lecture/**").permitAll()
            .antMatchers("/notice/**").permitAll()
            .antMatchers("/admin/**").permitAll();
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
