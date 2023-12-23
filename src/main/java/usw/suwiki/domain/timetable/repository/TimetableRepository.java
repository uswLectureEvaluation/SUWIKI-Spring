package usw.suwiki.domain.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.timetable.entity.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
}
