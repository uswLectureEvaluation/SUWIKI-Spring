package usw.suwiki.domain.user.user.controller.dto;

import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.ResponseForm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserResponseDto {

    @Getter
    @Builder
    public static class UserInformationResponseForm {
        private String loginId;
        private String email;
        private Integer point;
        private Integer writtenEvaluation;
        private Integer writtenExam;
        private Integer viewExam;

        public static UserInformationResponseForm buildMyPageResponseForm(User user) {
            return UserInformationResponseForm.builder()
                    .loginId(user.getLoginId())
                    .email(user.getEmail())
                    .point(user.getPoint())
                    .writtenEvaluation(user.getWrittenEvaluation())
                    .writtenExam(user.getWrittenExam())
                    .viewExam(user.getViewExamCount())
                    .build();
        }
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
