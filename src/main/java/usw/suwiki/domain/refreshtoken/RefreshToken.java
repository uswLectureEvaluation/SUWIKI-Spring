package usw.suwiki.domain.refreshtoken;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
