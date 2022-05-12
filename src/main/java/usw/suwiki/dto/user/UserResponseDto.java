package usw.suwiki.dto.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
