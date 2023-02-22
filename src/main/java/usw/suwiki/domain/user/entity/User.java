package usw.suwiki.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    public void editRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public void disable(User user) {
        this.email = user.getEmail();
        this.loginId = user.getLoginId();
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
}
