package usw.suwiki.domain.userIsolation.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
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
}
