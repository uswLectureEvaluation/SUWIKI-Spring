package usw.suwiki.domain.user.service;

import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostRestrictForm;

public interface RestrictingUserService {
  void executeRestrictUserFromEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm, Long reportedUserId);

  void executeRestrictUserFromExamPost(ExamPostRestrictForm examPostRestrictForm, Long reportedUserId);
}
