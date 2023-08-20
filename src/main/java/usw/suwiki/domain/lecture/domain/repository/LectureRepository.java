package usw.suwiki.domain.lecture.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.lecture.domain.Lecture;

import javax.persistence.LockModeType;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>, LectureQueryRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Lecture findByIdPessimisticLock(Long id);
}
