package usw.suwiki.domain.user.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.global.exception.errortype.AccountException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.EMAIL_NOT_AUTHED;
import static usw.suwiki.global.exception.ExceptionType.USER_POINT_LACK;
import static usw.suwiki.global.util.passwordfactory.PasswordRandomizer.randomizePassword;

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

    @CreatedDate
    @Column
    private LocalDateTime createdAt;

    @UpdateTimestamp
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

    public void waitQuit() {
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

    public void sleep() {
        this.loginId = null;
        this.password = null;
        this.email = null;
    }

    public void awake(String loginId, String password, String email) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
    }

    public void updateWritingEvaluatePost() {
        final int WRITE_EVALUATE_POST = 10;
        this.point += WRITE_EVALUATE_POST;
        this.writtenEvaluation += 1;
    }

    public void increasePointByWritingExamPost() {
        this.point += 20;
        this.writtenExam += 1;
    }

    public void purchaseExamPost() {
        final int examPostRequiringPoint = 20;
        if (this.point < examPostRequiringPoint) {
            throw new AccountException(USER_POINT_LACK);
        }
        this.point -= examPostRequiringPoint;
        this.viewExamCount += 1;
    }

    public void decreasePointAndWrittenEvaluationByDeleteEvaluatePosts() {
        final int deletePostRequiringPoint = 30;
        if (this.point < deletePostRequiringPoint) {
            throw new AccountException(USER_POINT_LACK);
        }
        this.point -= deletePostRequiringPoint;
        this.writtenEvaluation -= 1;
    }

    public void decreasePointAndWrittenExamByDeleteExamPosts() {
        final int deletePostRequiringPoint = 30;
        if (this.point < deletePostRequiringPoint) {
            throw new AccountException(USER_POINT_LACK);
        }
        this.point -= deletePostRequiringPoint;
        this.writtenExam -= 1;
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
        return bCryptPasswordEncoder.matches(inputPassword, this.password);
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
            throw new AccountException(EMAIL_NOT_AUTHED);
        }
        throw new AccountException(EMAIL_NOT_AUTHED);
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
