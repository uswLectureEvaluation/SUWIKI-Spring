package usw.suwiki.domain.user.user.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserResponseDto {

    @Getter
    @Builder
    public static class MyPageResponseForm {
        private String loginId;
        private String email;
        private Integer point;
        private Integer writtenEvaluation;
        private Integer writtenExam;
        private Integer viewExam;
    }

    @Getter
    @Builder
    public static class LoadMyRestrictedReasonResponseForm {
        private String restrictedReason;
        private String judgement;
        private LocalDateTime createdAt;
        private LocalDateTime restrictingDate;
    }

    @Getter
    @Builder
    public static class LoadMyBlackListReasonResponseForm {
        private String blackListReason;
        private String judgement;
        private LocalDateTime createdAt;
        private LocalDateTime expiredAt;
    }
}
