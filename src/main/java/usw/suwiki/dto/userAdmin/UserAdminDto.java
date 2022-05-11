package usw.suwiki.dto.userAdmin;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class UserAdminDto {
    @Data
    public static class BannedTargetForm {
        private Long evaluateIdx; //강의평가 인덱스
        private Long examIdx; //시험 인덱스
        private Boolean postType; //게시글 타입
        private Long bannedTime; //정지 기간
    }

    @Data
    public static class GetReportedPostsForm {
        private Long id;
        private Long evaluateIdx;
        private Long examIdx;
        private String professor;
        private String lectureName;
        private boolean postType;
        private LocalDateTime reportedDate;
        private String content;
    }

}
