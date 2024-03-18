package usw.suwiki.domain.lecture.schedule.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "business")
@RequiredArgsConstructor
public class SemesterProvider {
  private final String currentSemester;
}
