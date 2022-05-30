package usw.suwiki.domain.email;

import lombok.*;
import usw.suwiki.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_idx")
    private User user;

//    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, User user) {
//        this.token = token;
//        this.createdAt = createdAt;
//        this.expiresAt = expiresAt;
//        this.user = user;
//    }
}
