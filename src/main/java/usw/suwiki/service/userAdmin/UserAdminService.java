package usw.suwiki.service.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistDomain.BlacklistDomain;
import usw.suwiki.domain.user.User;
import usw.suwiki.dto.userAdmin.UserAdminDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.repository.blacklist.BlacklistRepository;
import usw.suwiki.repository.evaluation.EvaluatePostsRepository;
import usw.suwiki.repository.exam.ExamPostsRepository;
import usw.suwiki.repository.userAdmin.UserAdminEvaluateRepository;
import usw.suwiki.repository.userAdmin.UserAdminExamRepository;
import usw.suwiki.service.evaluation.EvaluatePostsService;
import usw.suwiki.service.exam.ExamPostsService;
import usw.suwiki.service.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminService {


    private final UserService userService;
    private final BlacklistRepository blacklistRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;

    private final UserAdminEvaluateRepository userAdminEvaluateRepository;
    private final UserAdminExamRepository userAdminExamRepository;
    private final EvaluatePostsRepository evaluatePostsRepository;
    private final ExamPostsRepository examPostsRepository;

    //신고받은 유저 데이터 -> 블랙리스트 테이블로 해싱
    @Transactional
    public void banUser(UserAdminDto.BannedTargetForm bannedTargetForm) {

        User user = userService.loadUserFromUserIdx(bannedTargetForm.getUserIdx());

        user.setRestricted(true);

        //이메일 해싱
        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());

        //블랙리스트 도메인 데이터 생성
        BlacklistDomain blacklistDomain = BlacklistDomain.builder()
                .user(user)
                .hashedEmail(hashTargetEmail)
                .build();

        //이메일 해싱 값 블랙리스트 테이블에 넣기
        blacklistRepository.save(blacklistDomain);

        //유저 index 로 객체 받아오기
        if (blacklistRepository.findByUserId(user.getId()).isEmpty())
            throw new AccountException(ErrorType.USER_NOT_EXISTS);

        //Optional 객체 받아오기
        Optional<BlacklistDomain> expiredAtSetTarget = blacklistRepository.findByUserId(user.getId());

        //index 로 받온 객체에 제한 시간 걸기
        expiredAtSetTarget.get().setExpiredAt(LocalDateTime.now().plusDays(bannedTargetForm.getBannedTime()));

    }

    //신고받은 게시글 삭제 해주기
    @Transactional
    public void banPost(UserAdminDto.BannedTargetForm bannedTargetForm) {
        // 포스트 타입이 true == 강의평가
        // 포스트 타입이 false == 시험정보

        //강의평가에 대한 게시글 삭제
        if (bannedTargetForm.getPostType()) {
            evaluatePostsService.deleteById(bannedTargetForm.getEvaluateIdx(),bannedTargetForm.getUserIdx());
//            userAdminEvaluateRepository.deleteById(bannedTargetForm.getEvaluateIdx());
//
//            User user = userService.loadUserFromUserIdx(bannedTargetForm.getUserIdx());
//            user.setBannedCount(+1);
//            user.setWrittenEvaluation(-1);
        }
        //시험정보에 대한 게시글 삭제
        else {
            examPostsService.deleteById(bannedTargetForm.getEvaluateIdx(),bannedTargetForm.getUserIdx());
//            userAdminExamRepository.deleteById(bannedTargetForm.getExamIdx());
//
//
//            User user = userService.loadUserFromUserIdx(bannedTargetForm.getUserIdx());
//            user.setBannedCount(+1);
//            user.setWrittenExam(-1);
        }
    }
}
