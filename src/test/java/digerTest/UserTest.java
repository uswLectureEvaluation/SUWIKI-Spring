package digerTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.SuwikiApplication;
import usw.suwiki.domain.blacklistDomain.BlacklistRepository;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.userIsolation.UserIsolation;
import usw.suwiki.domain.userIsolation.UserIsolationRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(classes = SuwikiApplication.class)
public class UserTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserIsolationRepository userIsolationRepository;

    @Autowired
    BlacklistRepository blacklistRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Test
    void checkLoginIdTest() {
        String loginId = "diger";
        Assertions.assertThat(userRepository.findByLoginId(loginId).isPresent() ||
                userIsolationRepository.findByLoginId(loginId).isPresent()).isEqualTo(false);
    }

    @Test
    void checkEmailTest() {

        String email = "diger@suwon.ac.kr";
        Assertions.assertThat(userRepository.findByEmail(email).isPresent() ||
                userIsolationRepository.findByLoginId(email).isPresent() ||
                blacklistRepository.findByHashedEmail(bCryptPasswordEncoder.encode(email)).isPresent()).isEqualTo(false);
    }

    @Test
    void loginTest() {

        String loginId = "diger";
        String password = "digerPW";

        Optional<User> user = userRepository.findByLoginId(loginId);

        // 유저 테이블에 있으면
        if (user.isPresent()) {
            if (bCryptPasswordEncoder.matches(password, user.get().getPassword())) {
                Assertions.assertThat(bCryptPasswordEncoder.matches(password, user.get().getPassword())).isEqualTo(false);
            }
        }
        // 유저 테이블에 없으면 휴면계정 테이블에서 찾기
        else if (user.isEmpty()) {
            Optional<UserIsolation> userIsolation = userIsolationRepository.findByLoginId(loginId);

            // 휴면계정에 있으면
            if (userIsolation.isPresent()) {
                if (bCryptPasswordEncoder.matches(password, userIsolation.get().getPassword())) {
                    // 휴면계정에서 원래 계정으로 돌리는 로직 추가
                    Assertions.assertThat(bCryptPasswordEncoder.matches(password, userIsolation.get().getPassword())).isEqualTo(false);}
            }
        }
    }

    @Test
    void userCountTest() {
        long count;
        boolean flag = false;

        count = userRepository.countUser() + userIsolationRepository.countUserIsolation();

        if (count > 0) {
            System.out.println("count = " + count);
            flag = true;
        } else if (count <= 0) {
            flag = false;
        }

        assertThat(flag).isEqualTo(true);
    }
}
