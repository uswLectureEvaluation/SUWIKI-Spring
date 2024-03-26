package usw.suwiki.domain.lecture.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "business")
@RequiredArgsConstructor
public class SemesterProvider { // todo: application에서 끌어오기
  private final String currentSemester;

  public String semester() {
    return currentSemester;
  }
}
