package usw.suwiki.domain.blacklistDomain;

import lombok.*;
import usw.suwiki.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Auto Increment

    @Column
    private String hashedEmail;

    @Column
    private LocalDateTime expiredAt;

    @OneToOne
    @JoinColumn(name = "user_idx")
    private User user;
}
