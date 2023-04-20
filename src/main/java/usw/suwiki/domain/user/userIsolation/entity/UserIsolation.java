package usw.suwiki.domain.user.userIsolation.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIsolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long userIdx;
    @Column
    private String loginId;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private LocalDateTime requestedQuitDate;
    @Column
    private LocalDateTime lastLogin;

    public boolean validatePassword(
        BCryptPasswordEncoder bCryptPasswordEncoder,
        String inputPassword
    ) {
        return this.password.equals(inputPassword);
    }
}
