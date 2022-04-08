package usw.suwiki.domain.refreshToken;

import lombok.*;
import usw.suwiki.domain.user.User;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payload;

    @OneToOne
    @JoinColumn(name = "user_idx")
    private User user;
}
