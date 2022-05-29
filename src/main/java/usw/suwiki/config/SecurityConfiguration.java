package usw.suwiki.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable() // Rest API 적용
                .csrf().disable() //API 서버이므로 CSRF 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //세션 사용X

        http
                .authorizeRequests()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/lecture/**").permitAll()
                .antMatchers("/exam-posts/**").permitAll()
                .antMatchers("/evaluate-posts/**").permitAll()
                .antMatchers("/notice/write").permitAll()
                .antMatchers("/notice/update").permitAll()
                .antMatchers("/notice/delete").permitAll()
                .antMatchers("/notice").permitAll();

    }
}