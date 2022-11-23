package usw.suwiki.domain.blacklistDomain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Auto Increment

    @Column
    private Long userIdx;

    @Column
    private String hashedEmail;

    @Column
    private LocalDateTime expiredAt;

    @Column
    private String bannedReason;

    @Column
    private String judgement;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
