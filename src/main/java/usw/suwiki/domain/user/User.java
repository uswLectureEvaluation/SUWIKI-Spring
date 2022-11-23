package usw.suwiki.domain.user;

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
    private Long id; //Auto Increment

    @Column
    private String loginId; //Login Id

    @Column
    private String password; //Login Password

    @Column
    private String email; //Email

    @Column
    private boolean restricted; //이메일 인증 유무 + 제재유저 대상

    @Column
    private Integer restrictedCount; //정지횟수

    @Enumerated(EnumType.STRING)
    private Role role; //User Auth

    @Column
    private Integer writtenEvaluation;

    @Column
    private Integer writtenExam;

    @Column
    private Integer viewExamCount;

    @Column
    private Integer point;

    @Column
    private LocalDateTime lastLogin; //Last Login Time

    @Column
    private LocalDateTime requestedQuitDate; //회원탈퇴 요청 일시

    @Column
    private LocalDateTime createdAt; //Join Time

    @Column
    private LocalDateTime updatedAt;
}
