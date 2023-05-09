package usw.suwiki.domain.refreshToken;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String payload;

    @Column
    private Long userIdx;

    public void updatePayload(String payload) {
        this.payload = payload;
    }

    public static RefreshToken buildRefreshToken(Long userIdx, String payload) {
        return RefreshToken.builder()
                .userIdx(userIdx)
                .payload(payload)
                .build();
    }
}
