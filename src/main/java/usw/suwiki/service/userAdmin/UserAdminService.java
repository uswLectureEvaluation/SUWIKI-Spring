package usw.suwiki.service.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistDomain.BlacklistDomain;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.exam.ExamPosts;
import usw.suwiki.domain.user.User;
import usw.suwiki.dto.userAdmin.UserAdminDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.repository.blacklist.BlacklistRepository;
import usw.suwiki.service.evaluation.EvaluatePostsService;
import usw.suwiki.service.exam.ExamPostsService;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminService {

    private final BlacklistRepository blacklistRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;

    //신고받은 유저 데이터 -> 블랙리스트 테이블로 해싱
    @Transactional
    public void banUser(UserAdminDto.BannedTargetForm bannedTargetForm) {

        User user = new User();

        if (bannedTargetForm.getPostType()) {
            //타겟 유저 인덱스로 유저 객체 불러오기
            EvaluatePosts evaluatePosts = evaluatePostsService.findById(bannedTargetForm.getEvaluateIdx());
            user = evaluatePosts.getUser();
        } else {
            //타겟 유저 인덱스로 유저 객체 불러오기
            ExamPosts examPosts = examPostsService.findById(bannedTargetForm.getEvaluateIdx());
            user = examPosts.getUser();
        }

        //이메일 해싱
        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());

        //이메일 해싱 값 블랙리스트 테이블에 넣기
        blacklistRepository.insertIntoHashEmailAndExpiredAt(hashTargetEmail, user.getId());

        //유저 index 로 객체 받아오기
        if (blacklistRepository.findByUserId(user.getId()).isEmpty()) throw new AccountException(ErrorType.USER_NOT_EXISTS);

        Optional<BlacklistDomain> expiredAtSetTarget = blacklistRepository.findByUserId(user.getId());

        //index 로 받온 객체에 제한 시간 걸기
        expiredAtSetTarget.get().setExpiredAt(bannedTargetForm.getBannedTime());
    }

    //신고받은 게시글 삭제 해주기
    @Transactional
    public void banPost(UserAdminDto.BannedTargetForm bannedTargetForm) {
        // 포스트 타입이 true == 강의평가
        // 포스트 타입이 false == 시험정보

        if (bannedTargetForm.getPostType()) {
            evaluatePostsService.deleteById(bannedTargetForm.getEvaluateIdx());
        } else {
            examPostsService.deleteById(bannedTargetForm.getExamIdx());
        }
    }
}
