package usw.suwiki.domain.evaluatepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.service.ClearEvaluatePostsService;

@Service
@Transactional
@RequiredArgsConstructor
class ClearEvaluatePostsServiceImpl implements ClearEvaluatePostsService {
  private final EvaluatePostService evaluatePostService;

  @Override
  public void clear(Long userId) {
    evaluatePostService.deleteAllByUserId(userId);
  }
}
