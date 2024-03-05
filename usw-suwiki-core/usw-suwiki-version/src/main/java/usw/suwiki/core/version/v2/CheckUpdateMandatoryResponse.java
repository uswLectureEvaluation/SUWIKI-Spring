package usw.suwiki.core.version.v2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckUpdateMandatoryResponse {
    private final Boolean isUpdateMandatory;

    public static CheckUpdateMandatoryResponse from(boolean isUpdateMandatory) {
        return new CheckUpdateMandatoryResponse(isUpdateMandatory);
    }
}
