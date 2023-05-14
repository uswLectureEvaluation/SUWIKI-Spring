package usw.suwiki.global;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@MappedSuperclass   //Jpa Entity 클래스들이 BastTimeEntity 를 상속할 경우, 필드들도 칼럼으로 인식된다.
@EntityListeners(AuditingEntityListener.class)  //Auditing 기능을 포함시킨다.
public abstract class BaseTimeEntity {

    @CreatedDate    // Entity가 생성되어 저장할 때 시간이 자동 저장된다.
    private LocalDateTime createDate;
}
