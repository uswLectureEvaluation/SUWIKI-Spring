package usw.suwiki.dto.view_exam;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PurchaseHistoryDto {
    private String professor;
    private String lectureName;
    private LocalDateTime createDate;


    @Builder
    public PurchaseHistoryDto(String professor, String lectureName, LocalDateTime createDate) {
        this.professor = professor;
        this.lectureName = lectureName;
        this.createDate = createDate;
    }
}
