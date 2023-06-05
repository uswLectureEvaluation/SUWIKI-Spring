package usw.suwiki.domain.user.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import usw.suwiki.domain.user.user.User;

import java.time.LocalDateTime;

@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
public class UserResponseDto {

    @Builder
    public record UserInformationResponseForm(
            String loginId,
            String email,
            Integer point,
            Integer writtenEvaluation,
            Integer writtenExam,
            Integer viewExam
    ) {

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

    @Builder
    public record LoadMyRestrictedReasonResponseForm(
            String restrictedReason,
            String judgement,
            LocalDateTime createdAt,
            LocalDateTime restrictingDate
    ) {

    }

    @Builder
    public record LoadMyBlackListReasonResponseForm(
            String blackListReason,
            String judgement,
            LocalDateTime createdAt,
            LocalDateTime expiredAt
    ) {
    }
}
