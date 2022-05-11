package usw.suwiki.service.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistDomain.BlacklistDomain;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.exam.ExamPosts;
import usw.suwiki.domain.reportTarget.ReportTarget;
import usw.suwiki.domain.user.User;
import usw.suwiki.dto.userAdmin.UserAdminDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.repository.blacklist.BlacklistRepository;
import usw.suwiki.repository.evaluation.EvaluatePostsRepository;
import usw.suwiki.repository.exam.ExamPostsRepository;
import usw.suwiki.repository.reportTarget.ReportTargetRepository;
import usw.suwiki.repository.userAdmin.UserAdminEvaluateRepository;
import usw.suwiki.repository.userAdmin.UserAdminExamRepository;
import usw.suwiki.service.evaluation.EvaluatePostsService;
import usw.suwiki.service.exam.ExamPostsService;
import usw.suwiki.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminService {


    // User 관련 서비스
    private final UserService userService;
    private final BlacklistRepository blacklistRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    // Post 관련 서비스
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;

    // Admin 관련 레포지토리
    private final ReportTargetRepository reportTargetRepository;


    // 신고받은 유저 데이터 -> 블랙리스트 테이블로 해싱
    @Transactional
    public void banUser(Long userIdx, Long bannedPeriod) {

        User user = userService.loadUserFromUserIdx(userIdx);

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
        expiredAtSetTarget.get().setExpiredAt(LocalDateTime.now().plusDays(bannedPeriod));

    }

    //신고받은 게시글 삭제 해주기
    @Transactional
    public Long banishPost(UserAdminDto.BannedTargetForm bannedTargetForm) {
        // 포스트 타입이 true == 강의평가
        // 포스트 타입이 false == 시험정보

        //강의평가에 대한 게시글 삭제
        if (bannedTargetForm.getPostType()) {

            // 추방할 게시글 불러오기
            EvaluatePosts targetedEvaluatePost = userService.loadEvaluatePostsByIndex(bannedTargetForm.getEvaluateIdx());

            // 강의평가를 작성한 작성자 인덱스 불러오기
            Long targetedUserIdx = targetedEvaluatePost.getUser().getId();

            // 강의 평가 삭제(작성한 게시글 갯수 감소, 포인트 감소 까지 반영)
            evaluatePostsService.deleteById(targetedEvaluatePost.getId(), targetedUserIdx);

            // 밴 횟수 증가
            increaseBannedTime(targetedUserIdx);

            return targetedUserIdx;
        }

        //시험정보에 대한 게시글 삭제
        else {

            // 추방할 게시글 불러오기
            ExamPosts targetedExamPost = userService.loadExamPostsByIndex(bannedTargetForm.getExamIdx());

            // 시험정보를 작성한 작성자 인덱스 불러오기
            Long targetedUserIdx = targetedExamPost.getUser().getId();

            // 시험 정보 삭제(작성한 게시글 갯수 감소, 포인트 감소 까지 반영)
            examPostsService.deleteById(targetedExamPost.getId(), targetedUserIdx);

            // 밴 횟수 증가
            increaseBannedTime(targetedUserIdx);

            return targetedUserIdx;
        }
    }

    // 밴 처리 후 밴 횟수 +1, 작성한 강의평가 -1
    @Transactional
    public void increaseBannedTime(Long userIdx) {
        User user = userService.loadUserFromUserIdx(userIdx);
        user.setBannedCount(user.getBannedCount() + 1);
    }

    //신고 받은 게시물 모두 불러오기
    @Transactional
    public List<ReportTarget> getReportedPostList() {
        return reportTargetRepository.loadAllReportedPosts();
    }
}
