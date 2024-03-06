package usw.suwiki.domain.user.service;

public interface RestrictingUserService {
  void executeRestrictUserFromEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm, Long reportedUserId);

  void executeRestrictUserFromExamPost(ExamPostRestrictForm examPostRestrictForm, Long reportedUserId);
}
