package usw.suwiki.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

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
                .antMatchers("/user/**").permitAll()
                .antMatchers("/evaluate-posts/**").permitAll()
                .antMatchers("/exam-posts/**").permitAll()
                .antMatchers("/notice/**").permitAll()
                .antMatchers("/lecture/**").permitAll()
                .antMatchers("/notice/**").permitAll()
                .antMatchers("/admin/**").permitAll();
        return http.build();
    }

//    protected void configure(HttpSecurity http) throws Exception {
//
//        http
//                // Rest API 적용(기본 페이지 사용 X)
//                .httpBasic().disable()
//                // REST API 방식이므로 CSRF 보안 토큰 생성 기능 종료
//                .csrf().disable()
//                //세션 사용X
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/user/**").permitAll()
//                .antMatchers("/evaluate-posts/**").permitAll()
//                .antMatchers("/exam-posts/**").permitAll()
//                .antMatchers("/notice/**").permitAll()
//                .antMatchers("/lecture/**").permitAll()
//                .antMatchers("/notice/**").permitAll()
//                .antMatchers("/admin/**").permitAll()
//                .and()
//                .apply(new JwtSecurityConfig(authenticationManagerBuilder.getOrBuild()));
//    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}