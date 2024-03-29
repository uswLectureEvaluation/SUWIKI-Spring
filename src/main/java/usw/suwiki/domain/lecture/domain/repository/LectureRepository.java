package usw.suwiki.domain.lecture.domain.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.lecture.domain.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>, LectureCustomRepository {

    // TODO: 낙관적 락은 어떨지 고민해보기 (김영한님의 추천은 READ COMMITTED + 낙관적 락)
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    // TODO: timeout 필요성 동시성 테스트로 확인하기
    //    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="10000")})  // timeout: 10s
    Optional<Lecture> findForUpdateById(Long lectureId);

    List<Lecture> findAllBySemesterContains(String semester);
}
