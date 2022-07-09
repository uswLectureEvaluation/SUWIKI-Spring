package usw.suwiki.domain.userAdmin;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserAdminRequestDto {

    @Data
    public static class EvaluatePostNoProblemForm {
        private Long evaluateIdx; //강의평가 인덱스
    }

    @Data
    public static class ExamPostNoProblemForm {
        private Long examIdx; //시험정보 인덱스
    }

    @Data
    public static class EvaluatePostRestrictForm {
        private Long evaluateIdx; // 강의평가 게시글 인덱스
        private Long restrictingDate; // 정지 기간
        private String restrictingReason; // 정지 사유
        private String judgement; // 처벌
    }

    @Data
    public static class ExamPostRestrictForm {
        private Long examIdx; // 시험정보 게시글 인덱스
        private Long restrictingDate; // 정지 기간
        private String restrictingReason; // 정지 사유
        private String judgement; // 처벌
    }

    @Data
    public static class EvaluatePostBlacklistForm {
        private Long evaluateIdx; //강의평가 인덱스
        private String bannedReason; // 정지 사유 ( Ex) 허위 신고 누적으로 인한 블랙리스트 처리 안내)
        private String judgement; // 처벌
    }

    @Data
    public static class ExamPostBlacklistForm {
        private Long examIdx; //시험정보 인덱스
        private String bannedReason; // 정지 사유 ( Ex) 허위 신고 누적으로 인한 블랙리스트 처리 안내)
        private String judgement; // 처벌
    }
}
