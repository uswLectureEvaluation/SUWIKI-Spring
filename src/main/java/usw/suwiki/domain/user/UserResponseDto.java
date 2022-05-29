package usw.suwiki.domain.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class UserResponseDto {

    @Data @Builder
    public static class MyPageResponse {
        private String loginId; // 유저 로그인 아이디
        private String email;
        private Integer point; //유저 포인트
        private Integer writtenEvaluation; //유저 작성한 강의평가 갯수
        private Integer writtenExam; //유저 작성한 시험정보 갯수
        private Integer viewExam; //조회환 시험정보 갯수
    }

    @Data
    public static class ViewMyBannedReasonForm {
        private LocalDateTime bannedAt; //밴 맞은 시각
        private LocalDateTime bannedUntil; // 밴 풀리는 시각
        private String judgementTitle; // 정지사유 타이틀
        private String judgement; // 조치사항
    }
}
