package usw.suwiki.domain.lecture.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.lecture.domain.LectureSchedule;

public interface LectureScheduleRepository extends JpaRepository<LectureSchedule, Long> {

}
