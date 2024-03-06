package usw.suwiki.domain.user.isolated;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.secure.encode.PasswordEncoder;
import usw.suwiki.secure.random.PasswordRandomizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public boolean validatePassword(PasswordEncoder passwordEncoder, String inputPassword) {
        return passwordEncoder.matches(inputPassword, password);
    }

    public String updateRandomPassword(PasswordEncoder passwordEncoder) {
        String generatedPassword = PasswordRandomizer.randomizePassword();
        this.password = passwordEncoder.encode(generatedPassword);
        return generatedPassword;
    }
}
