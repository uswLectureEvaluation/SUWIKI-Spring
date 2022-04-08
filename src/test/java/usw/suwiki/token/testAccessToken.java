package usw.suwiki.token;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.SuwikiApplication;
import usw.suwiki.domain.user.Role;
import usw.suwiki.domain.user.User;
import usw.suwiki.jwt.JwtTokenProvider;

@SpringBootTest(classes = SuwikiApplication.class)
public class testAccessToken {

//    @Autowired
//    JwtTokenProvider jwtTokenProvider;
//
//    @Test
//    public void test() {
//        User user = User.builder()
//                .id(1L)
//                .loginId("abcd")
//                .restricted(false)
//                .role(Role.USER)
//                .build();
//
//        String AccessToken = jwtTokenProvider.createAccessToken(user);
//
//        System.out.println(AccessToken);
//
//        jwtTokenResolver.getId(AccessToken);
//
//
//    }

}
