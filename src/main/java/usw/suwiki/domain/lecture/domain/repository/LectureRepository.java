package usw.suwiki.domain.lecture.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.lecture.domain.Lecture;

import javax.persistence.LockModeType;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>, LectureQueryRepository {

    @Query(value = "SELECT * FROM lecture WHERE id = :id FOR UPDATE", nativeQuery = true)
    @Lock(LockModeType.PESSIMISTIC_READ)
    Lecture findByIdPessimisticLock(@Param("id") Long id);
}
