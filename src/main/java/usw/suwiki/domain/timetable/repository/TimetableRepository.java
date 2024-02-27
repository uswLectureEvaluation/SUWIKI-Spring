package usw.suwiki.domain.timetable.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.timetable.entity.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findAllByUserId(Long userId);

}
