package usw.suwiki.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import usw.suwiki.auth.token.ConfirmationToken;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.secure.PasswordEncoder;
import usw.suwiki.core.secure.PasswordRandomizer;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.lecture.timetable.Timetable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Timetable> timetableList = new ArrayList<>();

    @CreatedDate
    @Column
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EvaluatePost> evaluatePostList = new ArrayList<>();


    public static User makeUser(String loginId, String password, String email) {
        return builder()
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

    /**
     * User Status
     */
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
        updateLastLoginDate();
    }

    public void activateUser() {
        this.restricted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.role = Role.USER;
    }

    public boolean isAdmin() {
        return this.role.equals(Role.ADMIN);
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String newPassword) {
        this.password = passwordEncoder.encode(newPassword);
    }

    public String updateRandomPassword(PasswordEncoder passwordEncoder) {
        String generatedPassword = PasswordRandomizer.randomizePassword();
        this.password = passwordEncoder.encode(generatedPassword);
        return generatedPassword;
    }

    public boolean validatePassword(PasswordEncoder passwordEncoder, String inputPassword) {
        return passwordEncoder.matches(inputPassword, this.password);
    }

    public boolean isUserEmailAuthed(Optional<ConfirmationToken> confirmationToken) {
        if (confirmationToken.isPresent()) {
            if (confirmationToken.get().isVerified()) {
                return true;
            }
            throw new AccountException(ExceptionType.EMAIL_NOT_AUTHED);
        }
        throw new AccountException(ExceptionType.EMAIL_NOT_AUTHED);
    }

    public void updateLastLoginDate() {
        this.lastLogin = LocalDateTime.now();
    }

    public void addEvaluatePost(EvaluatePost evaluatePost) {
        this.evaluatePostList.add(evaluatePost);
    }

    public void removeEvaluatePost(EvaluatePost evaluatePost) {
        this.evaluatePostList.remove(evaluatePost);
    }

    public void updateWritingEvaluatePost() {
        final int WRITE_EVALUATE_POST = 10;
        this.point += WRITE_EVALUATE_POST;
        this.writtenEvaluation += 1;
    }

    public void decreasePointAndWrittenEvaluationByDeleteEvaluatePosts() {
        final int deletePostRequiringPoint = 30;
        if (this.point < deletePostRequiringPoint) {
            throw new AccountException(ExceptionType.USER_POINT_LACK);
        }
        this.point -= deletePostRequiringPoint;
        this.writtenEvaluation -= 1;
    }

    public void increasePointByWritingExamPost() {
        this.point += 20;
        this.writtenExam += 1;
    }

    public void purchaseExamPost() {
        final int examPostRequiringPoint = 20;
        if (this.point < examPostRequiringPoint) {
            throw new AccountException(ExceptionType.USER_POINT_LACK);
        }
        this.point -= examPostRequiringPoint;
        this.viewExamCount += 1;
    }

    public void decreasePointAndWrittenExamByDeleteExamPosts() {
        final int deletePostRequiringPoint = 30;
        if (this.point < deletePostRequiringPoint) {
            throw new AccountException(ExceptionType.USER_POINT_LACK);
        }
        this.point -= deletePostRequiringPoint;
        this.writtenExam -= 1;
    }

    public void increaseRestrictedCountByReportedPost() {
        this.restrictedCount += 1;
    }

    public void increasePointByReporting() {
        this.point += 1;
    }

    public void addTimetable(Timetable timetable) {
        this.timetableList.add(timetable);
    }

    public void removeTimetable(Timetable timetable) {
        this.timetableList.remove(timetable);
    }

}
