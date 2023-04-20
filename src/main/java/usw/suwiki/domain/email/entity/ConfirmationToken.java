package usw.suwiki.domain.email.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.user.entity.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userIdx;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    public static ConfirmationToken makeToken(User user) {
        return ConfirmationToken.builder()
            .token(UUID.randomUUID().toString())
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(15))
            .userIdx(user.getId())
            .build();
    }

    public void updateConfirmedAt() {
        this.confirmedAt = LocalDateTime.now();
    }

    public boolean isExpiredAt() {
        LocalDateTime expiredAt = this.getExpiresAt();
        return expiredAt.isBefore(LocalDateTime.now());
    }

    public boolean isVerified() {
        return this.confirmedAt != null;
    }
}
