package usw.suwiki.domain.evaluatepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.core.exception.EvaluatePostException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.evaluatepost.EvaluatePostQueryRepository;
import usw.suwiki.domain.evaluatepost.EvaluatePostRepository;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostRequest;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponse;
import usw.suwiki.domain.lecture.data.EvaluatedData;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.report.EvaluatePostReport;
import usw.suwiki.domain.report.service.ReportService;
import usw.suwiki.domain.user.service.UserBusinessService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EvaluatePostService {
  private final EvaluatePostRepository evaluatePostRepository;
  private final EvaluatePostQueryRepository evaluatePostQueryRepository;

  private final UserBusinessService userBusinessService;
  private final LectureService lectureService;

  private final ReportService reportService;

  @Transactional(readOnly = true)
  public EvaluatePostResponse.Details loadAllEvaluatePostsByLectureId(PageOption option, Long userId, Long lectureId) {
    return new EvaluatePostResponse.Details(
      evaluatePostQueryRepository.findAllByLectureIdAndPageOption(lectureId, option.getOffset()),
      isAlreadyWritten(userId, lectureId)
    );
  }

  @Transactional(readOnly = true)
  public List<EvaluatePostResponse.MyPost> loadAllEvaluatePostsByUserId(PageOption option, Long userId) {
    return evaluatePostQueryRepository.findAllByUserIdAndPageOption(userId, option.getOffset());
  }

  public void report(Long reportingUserId, Long evaluateId) {
    EvaluatePost evaluatePost = loadEvaluatePostById(evaluateId);
    Long reportedUserId = evaluatePost.getUserId();

    reportService.saveEvaluatePostReport(EvaluatePostReport.of( // todo: 의존성 분리하기
      evaluateId,
      reportedUserId,
      reportingUserId,
      evaluatePost.getProfessor(),
      evaluatePost.getLectureName(),
      evaluatePost.getContent()
    ));
  }

  public void write(Long userId, Long lectureId, EvaluatePostRequest.Create request) {
    if (isAlreadyWritten(userId, lectureId)) {
      throw new EvaluatePostException(ExceptionType.POSTS_WRITE_OVERLAP);
    }

    EvaluatePost evaluatePost = EvaluatePostMapper.toEntity(userId, lectureId, request);
    EvaluatedData evaluatedData = EvaluatePostMapper.toEvaluatedData(evaluatePost.getLectureRating());

    lectureService.evaluate(lectureId, evaluatedData);
    evaluatePostRepository.save(evaluatePost);
    userBusinessService.wroteEvaluation(userId);
  }

  private boolean isAlreadyWritten(Long userId, Long lectureId) {
    return evaluatePostRepository.existsByUserIdAndLectureInfoLectureId(userId, lectureId);
  }

  public void update(Long evaluateId, EvaluatePostRequest.Update request) {
    EvaluatePost evaluatePost = loadEvaluatePostById(evaluateId);
    EvaluatedData currentEvaluation = EvaluatePostMapper.toEvaluatedData(evaluatePost.getLectureRating());

    evaluatePost.update(
      evaluatePost.getContent(),
      evaluatePost.getLectureName(),
      request.getSelectedSemester(),
      evaluatePost.getProfessor(),
      EvaluatePostMapper.toRating(request)
    );

    EvaluatedData updatedEvaluation = EvaluatePostMapper.toEvaluatedData(evaluatePost.getLectureRating());
    lectureService.updateEvaluation(evaluatePost.getLectureId(), currentEvaluation, updatedEvaluation);
  }

  public void deleteEvaluatePost(Long evaluateId, Long userId) {
    EvaluatePost evaluatePost = loadEvaluatePostById(evaluateId);
    evaluatePost.validateAuthor(userId);

    delete(evaluatePost);
    userBusinessService.deleteEvaluation(userId);
  }

  public void delete(EvaluatePost evaluatePost) {
    evaluatePostRepository.delete(evaluatePost);
  }

  public void deleteAllByUserId(Long userId) {
    List<EvaluatePost> evaluatePosts = evaluatePostRepository.findAllByUserId(userId);
    evaluatePostRepository.deleteAllInBatch(evaluatePosts);
  }

  public EvaluatePost loadEvaluatePostById(Long evaluateId) {
    return evaluatePostRepository.findById(evaluateId)
      .orElseThrow(() -> new EvaluatePostException(ExceptionType.EVALUATE_POST_NOT_FOUND));
  }
}
