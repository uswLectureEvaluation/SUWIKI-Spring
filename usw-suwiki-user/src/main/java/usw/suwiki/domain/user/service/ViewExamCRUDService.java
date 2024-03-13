package usw.suwiki.domain.user.service;

import usw.suwiki.domain.user.viewexam.ViewExam;

import java.util.List;

public interface ViewExamCRUDService {
  boolean isExist(Long userId, Long lectureId);

  void save(ViewExam viewExam);

  List<ViewExam> loadViewExamsFromUserIdx(Long userIdx);

  void deleteAllFromUserIdx(Long userIdx);
}
