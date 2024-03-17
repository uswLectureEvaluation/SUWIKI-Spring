package usw.suwiki.domain.report.model;

public record Report(
  Long targetId,
  Long reportedUserId,
  Long reportingUserId,
  String content,
  String lecture,
  String professor
) {
  public static Report exam(Long examId, Long reportedUserId, Long reportingUserId, String content, String lecture, String professor) {
    return new Report(examId, reportedUserId, reportingUserId, content, lecture, professor);
  }

  public static Report evaluate(Long evaluateId, Long reportedUserId, Long reportingUserId, String content, String lecture, String professor) {
    return new Report(evaluateId, reportedUserId, reportingUserId, content, lecture, professor);
  }
}
