package usw.suwiki.domain.apilogger;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiLogger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long callTime;

    @Column
    private Long processAvg;

    @Column
    private LocalDate callDate;

    public void calculateProcessAvg(Long currentProcessTime) {
        this.processAvg = (currentProcessTime + (processAvg * callTime)) / (this.callTime + 1);
        this.callTime += 1;
    }
}
