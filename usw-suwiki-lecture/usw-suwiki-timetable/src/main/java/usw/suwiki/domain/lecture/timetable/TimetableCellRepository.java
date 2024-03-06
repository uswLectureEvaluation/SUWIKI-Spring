package usw.suwiki.domain.lecture.timetable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableCellRepository extends JpaRepository<TimetableCell, Long> {

    List<TimetableCell> findAllByTimetableId(Long timetableId);
}
