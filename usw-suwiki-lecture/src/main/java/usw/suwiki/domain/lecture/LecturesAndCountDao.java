package usw.suwiki.domain.lecture;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class LecturesAndCountDao {
    private final List<Lecture> lectureList;
    private final Long count;
}
