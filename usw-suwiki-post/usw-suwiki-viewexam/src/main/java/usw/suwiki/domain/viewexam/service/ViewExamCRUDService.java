package usw.suwiki.domain.viewexam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.viewexam.ViewExam;
import usw.suwiki.domain.viewexam.ViewExamRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ViewExamCRUDService {
  private final ViewExamRepository viewExamRepository;

  public boolean isExist(Long userId, Long lectureId) {
    return viewExamRepository.validateIsExists(userId, lectureId);
  }

  @Transactional
  public void save(ViewExam viewExam) {
    viewExamRepository.save(viewExam);
  }
}
