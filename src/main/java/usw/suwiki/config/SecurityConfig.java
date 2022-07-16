package usw.suwiki.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import usw.suwiki.global.jwt.JwtAuthenticationProvider;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public SecurityConfig(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            JwtAuthenticationProvider jsonWebTokenProvider
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.authenticationManagerBuilder.authenticationProvider(jsonWebTokenProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // Rest API 적용(기본 페이지 사용 X)
                .httpBasic().disable()

                // REST API 방식이므로 CSRF 보안 토큰 생성 기능 종료
                .csrf().disable()

                //세션 사용X
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

//                .authorizeRequests()
//                .antMatchers("/user/join").permitAll()
//                .antMatchers("/user/check-id").permitAll()
//                .antMatchers("/user/check-email").permitAll()
//                .antMatchers("/user/find-id").permitAll()
//                .antMatchers("/user/find-pw").permitAll()
//                .antMatchers("/user/login").permitAll()
//                .antMatchers("/user/verify-email/**").permitAll()
//                .antMatchers("/lecture/all/**").permitAll()
//                .antMatchers("/lecture/search/**").permitAll()
//                .antMatchers("/notice/all/**").permitAll()
//                .antMatchers("/suwiki/**").permitAll()
//
//                .anyRequest()
//                .hasAnyRole("ADMIN", "USER")
//
//
//                .and()
//
//                .apply(new JwtSecurityConfig(authenticationManagerBuilder.getOrBuild()));


                .authorizeRequests()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/evaluate-posts/**").permitAll()
                .antMatchers("/exam-posts/**").permitAll()
                .antMatchers("/notice/**").permitAll()
                .antMatchers("/lecture/**").permitAll()
                .antMatchers("/notice/**").permitAll()
                .antMatchers("/admin/**").permitAll()
                .and()
                .apply(new JwtSecurityConfig(authenticationManagerBuilder.getOrBuild()));


    }
}