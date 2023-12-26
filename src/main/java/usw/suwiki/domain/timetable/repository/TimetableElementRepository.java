package usw.suwiki.domain.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.timetable.entity.TimetableElement;

public interface TimetableElementRepository extends JpaRepository<TimetableElement, Long> {
}
