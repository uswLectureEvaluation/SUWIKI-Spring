package usw.suwiki.domain.user.user.entity;

import static usw.suwiki.global.exception.ExceptionType.USER_NOT_EMAIL_AUTHED;
import static usw.suwiki.global.exception.ExceptionType.USER_POINT_LACK;
import static usw.suwiki.global.util.passwordfactory.PasswordRandomizer.randomizePassword;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;
import usw.suwiki.global.exception.errortype.AccountException;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String loginId;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private Boolean restricted;

    @Column
    private Integer restrictedCount;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private Integer writtenEvaluation;

    @Column
    private Integer writtenExam;

    @Column
    private Integer viewExamCount;

    @Column
    private Integer point;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private LocalDateTime requestedQuitDate;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public static User makeUser(String loginId, String password, String email) {
        return User.builder()
            .loginId(loginId)
            .password(password)
            .email(email)
            .restricted(true)
            .restrictedCount(0)
            .writtenEvaluation(0)
            .writtenExam(0)
            .point(0)
            .viewExamCount(0)
            .build();
    }

    public void editRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public void disable() {
        this.restricted = true;
        this.restrictedCount = null;
        this.role = null;
        this.writtenExam = null;
        this.writtenEvaluation = null;
        this.viewExamCount = null;
        this.point = null;
        this.lastLogin = null;
        this.createdAt = null;
        this.updatedAt = null;
        this.requestedQuitDate = LocalDateTime.now();
    }

    public void increasePointByWritingEvaluatePost() {
        this.point += 10;
    }

    public void increasePointByWritingExamPost() {
        this.point += 20;
    }

    public void decreasePointByPurchaseExamPost() {
        final int examPostRequiringPoint = 20;
        if (this.point < examPostRequiringPoint) {
            throw new AccountException(USER_POINT_LACK);
        }
        this.point -= examPostRequiringPoint;
    }

    public void decreasePointByDeletePosts() {
        final int deletePostRequiringPoint = 30;
        if (this.point < deletePostRequiringPoint) {
            throw new AccountException(USER_POINT_LACK);
        }
        this.point -= deletePostRequiringPoint;
    }

    public void updatePassword(BCryptPasswordEncoder bCryptPasswordEncoder, String newPassword) {
        this.password = bCryptPasswordEncoder.encode(newPassword);
    }

    public String updateRandomPassword(BCryptPasswordEncoder bCryptPasswordEncoder) {
        String generatedPassword = randomizePassword();
        this.password = bCryptPasswordEncoder.encode(generatedPassword);
        return generatedPassword;
    }

    public boolean validatePassword(
        BCryptPasswordEncoder bCryptPasswordEncoder, String inputPassword
    ) {
        return bCryptPasswordEncoder.encode(inputPassword).matches(password);
    }

    public void activateUser() {
        this.restricted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.role = Role.USER;
    }

    public boolean isUserEmailAuthed(Optional<ConfirmationToken> confirmationToken) {
        if (confirmationToken.isPresent()) {
            if (confirmationToken.get().isVerified()) {
                return true;
            }
            throw new AccountException(USER_NOT_EMAIL_AUTHED);
        }
        throw new AccountException(USER_NOT_EMAIL_AUTHED);
    }

    public void updateLastLoginDate() {
        this.lastLogin = LocalDateTime.now();
    }

    public void increaseRestrictedCountByReportedPost() {
        this.restrictedCount += 1;
    }

    public void increasePointByReporting() {
        this.point += 1;
    }
}
