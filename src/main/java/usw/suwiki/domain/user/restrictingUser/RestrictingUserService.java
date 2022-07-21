package usw.suwiki.domain.user.restrictingUser;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.exam.ExamPosts;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.UserResponseDto;
import usw.suwiki.domain.user.UserService;
import usw.suwiki.domain.userAdmin.UserAdminRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestrictingUserService {

    private final RestrictingUserRepository restrictingUserRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    // 강의평가 게시글로 유저 정지 먹이기
    @Transactional
    public void addRestrictingTableByEvaluatePost(UserAdminRequestDto.EvaluatePostRestrictForm restrictForm) {

        EvaluatePosts evaluatePosts = userService.loadEvaluatePostsByIndex(restrictForm.getEvaluateIdx());
        User user = userService.loadUserFromUserIdx(evaluatePosts.getUser().getId());

        RestrictingUser restrictingUser = RestrictingUser.builder()
                .userIdx(user.getId())
                .restrictingDate(LocalDateTime.now().plusDays(restrictForm.getRestrictingDate()))
                .restrictingReason(restrictForm.getRestrictingReason())
                .judgement(restrictForm.getJudgement())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        restrictingUserRepository.save(restrictingUser);
    }

    // 시험정보 게시글로 유저 정지 먹이기
    @Transactional
    public void addRestrictingTableByExamPost(UserAdminRequestDto.ExamPostRestrictForm restrictForm) {

        ExamPosts examPosts = userService.loadExamPostsByIndex(restrictForm.getExamIdx());
        User user = userService.loadUserFromUserIdx(examPosts.getUser().getId());

        RestrictingUser restrictingUser = RestrictingUser.builder()
                .userIdx(user.getId())
                .restrictingDate(LocalDateTime.now().plusDays(restrictForm.getRestrictingDate()))
                .restrictingReason(restrictForm.getRestrictingReason())
                .judgement(restrictForm.getJudgement())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        restrictingUserRepository.save(restrictingUser);
    }

    // 정지내역 내역 모두보기 DTO 로 Typing
    @Transactional
    public List<UserResponseDto.ViewMyRestrictedReasonForm> getRestrictedLog(Long userIdx) {

        List<RestrictingUser> loadedDomain = restrictingUserRepository.findByUserIdx(userIdx);

        List<UserResponseDto.ViewMyRestrictedReasonForm> finalResultForm = new ArrayList<>();


        // 정지 내역 조회하기
        if (loadedDomain.toArray().length > 0) {

            for (RestrictingUser target : loadedDomain) {

                UserResponseDto.ViewMyRestrictedReasonForm resultForm = new UserResponseDto.ViewMyRestrictedReasonForm();

                String extractedBannedReason = target.getRestrictingReason();
                String extractedJudgement = target.getJudgement();
                LocalDateTime extractedCreatedAt = target.getCreatedAt();
                LocalDateTime restrictingDate = target.getRestrictingDate();

                // 정지사유
                resultForm.setRestrictedReason(extractedBannedReason);

                // 조치사항
                resultForm.setJudgement(extractedJudgement);

                // 정지 먹은 날짜
                resultForm.setCreatedAt(extractedCreatedAt);

                // 정지 해제 되는 시각
                resultForm.setRestrictingDate(restrictingDate);

                finalResultForm.add(resultForm);
            }
        }
        return finalResultForm;
    }

    // 이용정지를 풀기 위한 메서드 --> 정지 테이블에서 유저 삭제
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void isUnrestrictedTarget() {

        // 현재시각으로부터 - 30일
         LocalDateTime targetTime = LocalDateTime.now().minusDays(30);

        List<RestrictingUser> targetUser = restrictingUserRepository.findByRestrictingDateBefore(targetTime);

        for (RestrictingUser target : targetUser) {
            Long userIdx = target.getUserIdx();

            restrictingUserRepository.deleteByUserIdx(userIdx);
            userRepository.unRestricted(userIdx);
        }
    }
}
