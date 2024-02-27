package usw.suwiki.domain.timetable.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import usw.suwiki.domain.timetable.entity.TimetableCell;

public interface TimetableCellRepository extends JpaRepository<TimetableCell, Long> {

    List<TimetableCell> findAllByTimetableId(Long timetableId);
}
