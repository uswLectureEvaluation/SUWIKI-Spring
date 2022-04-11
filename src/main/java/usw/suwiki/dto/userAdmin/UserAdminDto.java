package usw.suwiki.dto.userAdmin;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserAdminDto {
    @Data
    public static class BannedTargetForm {
        private Long userIdx; //신고한 사람
        private Long evaluateIdx; //강의평가 인덱스
        private Long examIdx; //시험 인덱스
        private Boolean postType; //게시글 타입
        private Long bannedTime; //정지 기간
    }
}
