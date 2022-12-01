package usw.suwiki.domain.user.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
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
    private boolean restricted;
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
}
