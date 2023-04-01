package usw.suwiki.global;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//모든 Entity의 상위 클래스가 되어 Entity 들의 createdDate, modifiedDate를 자동으로 관리 하는 역할

@Getter
@MappedSuperclass   //Jpa Entity 클래스들이 BastTimeEntity 를 상속할 경우, 필드들도 칼럼으로 인식된다.
@EntityListeners(AuditingEntityListener.class)  //Auditing 기능을 포함시킨다.
public abstract class BaseTimeEntity {

    @CreatedDate    // Entity가 생성되어 저장할 때 시간이 자동 저장된다.
    private LocalDateTime createDate;
}
