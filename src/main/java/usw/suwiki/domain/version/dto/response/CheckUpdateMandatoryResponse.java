package usw.suwiki.domain.version.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckUpdateMandatoryResponse {

    private final Boolean isUpdateMandatory;

    public static CheckUpdateMandatoryResponse from(boolean isUpdateMandatory) {
        return CheckUpdateMandatoryResponse.builder()
            .isUpdateMandatory(isUpdateMandatory)
            .build();
    }
}
