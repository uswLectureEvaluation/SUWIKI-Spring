package usw.suwiki.domain.exam.controller.dto.viewexam;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
public class PurchaseHistoryDto {
    private Long id;
    private String professor;
    private String lectureName;
    private String majorType;
    private LocalDateTime createDate;

    @Builder
    public PurchaseHistoryDto(Long id, String professor, String lectureName, String majorType, LocalDateTime createDate) {
        this.id = id;
        this.professor = professor;
        this.lectureName = lectureName;
        this.majorType = majorType;
        this.createDate = createDate;
    }
}
