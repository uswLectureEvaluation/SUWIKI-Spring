package usw.suwiki.domain.userIsolation.entity;

import lombok.*;

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
    private Long id;

    //user Index (격리 테이블로 옮기면 유저 테이블에서 삭제해줘야하는데, 연관관계가 묶여있으면 유저테이블 삭제 시 격리테이블에서도 삭제됨)
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
}
