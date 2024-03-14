package usw.suwiki.domain.evaluatepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.evaluatepost.EvaluatePostRepository;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.service.ClearEvaluatePostsService;
import usw.suwiki.domain.user.service.UserCRUDService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
class ClearEvaluatePostsServiceImpl implements ClearEvaluatePostsService {
  private final EvaluatePostRepository evaluatePostRepository;
  private final UserCRUDService userCRUDService;

  @Override
  public void clear(Long userId) {
    User user = userCRUDService.loadUserFromUserIdx(userId);
    List<EvaluatePost> evaluatePosts = evaluatePostRepository.findAllByUser(user);
    evaluatePostRepository.deleteAllInBatch(evaluatePosts);
  }
}
