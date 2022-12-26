package usw.suwiki.domain.refreshToken.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomRefreshTokenRepository {

    String loadPayloadByUserIdx(Long id);

    void updatePayload(String newRefreshToken, Long id);
}
