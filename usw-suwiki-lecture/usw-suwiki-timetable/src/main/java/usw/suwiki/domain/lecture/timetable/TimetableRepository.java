package usw.suwiki.domain.lecture.timetable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findAllByUserId(Long userId);

}
