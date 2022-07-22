package usw.suwiki.domain.userIsolation;

import lombok.*;
import usw.suwiki.domain.user.Role;
import usw.suwiki.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserIsolation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Auto Increment

    private Long userIdx; //user Index (격리 테이블로 옮기면 유저 테이블에서 삭제해줘야하는데, 연관관계가 묶여있으면 유저테이블 삭제 시 격리테이블에서도 삭제됨)

    @Column
    private String loginId; //Login Id

    @Column
    private String password; //Login Password

    @Column
    private String email; //Email

    @Column
    private LocalDateTime requested_quit_date;

    @Column
    private LocalDateTime last_login;
}
