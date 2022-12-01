package usw.suwiki.domain.viewExam.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
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
