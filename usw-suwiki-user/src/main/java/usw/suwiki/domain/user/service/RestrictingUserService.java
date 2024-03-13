package usw.suwiki.domain.user.service;

import java.util.List;

import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostRestrictForm;

public interface RestrictingUserService {
  List<Long> loadAllRestrictedUntilNow();

  void releaseByUserId(Long userId);

  void executeRestrictUserFromEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm, Long reportedUserId);

  void executeRestrictUserFromExamPost(ExamPostRestrictForm examPostRestrictForm, Long reportedUserId);
}
