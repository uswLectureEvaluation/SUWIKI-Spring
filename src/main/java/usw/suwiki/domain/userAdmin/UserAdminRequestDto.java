package usw.suwiki.domain.userAdmin;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserAdminRequestDto {

    @Data
    public static class EvaluatePostBanForm {
        private Long evaluateIdx; //강의평가 인덱스
        private String bannedReason; // 정지 사유 ( Ex) 허위 신고 누적으로 인한 정지 안내)
        private String judgement; // 조치 사항 ( Ex) 30일 정지)
        private Long bannedTime; //정지 기간
    }

    @Data
    public static class ExamPostBanForm {
        private Long examIdx; //시험정보 인덱스
        private String bannedReason; // 정지 사유 ( Ex) 허위 신고 누적으로 인한 정지 안내)
        private String judgement; // 조치 사항 ( Ex) 30일 정지)
        private Long bannedTime; //정지 기간
    }

    @Data
    public static class ExamPostNoProblemForm {
        private Long examIdx; //시험정보 인덱스
    }

    @Data
    public static class EvaluatePostNoProblemForm {
        private Long evaluateIdx; //강의평가 인덱스
    }
}
