package usw.suwiki.domain.user.sleepingUser;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistDomain.BlackListService;
import usw.suwiki.domain.email.EmailSender;
import usw.suwiki.domain.emailBuild.BuildAutoDeletedWarningUserFormService;
import usw.suwiki.domain.emailBuild.BuildSoonDormantTargetFormService;
import usw.suwiki.domain.evaluation.EvaluatePostsService;
import usw.suwiki.domain.exam.ExamPostsService;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserDto;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.UserService;
import usw.suwiki.domain.userIsolation.UserIsolation;
import usw.suwiki.domain.userIsolation.UserIsolationRepository;
import usw.suwiki.domain.userIsolation.UserIsolationService;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SleepingUserService {

    // User
    private final UserService userService;
    private final UserRepository userRepository;

    // 휴면 계정
    private final UserIsolationService userIsolationService;
    private final UserIsolationRepository userIsolationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BuildSoonDormantTargetFormService buildSoonDormantTargetFormService;
    private final BuildAutoDeletedWarningUserFormService buildAutoDeletedWarningUserFormService;
    private final EmailSender emailSender;

    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;


    // 블랙리스트 계정
    private final BlackListService blackListService;


    // 본 테이블 -> 격리 테이블
    @Transactional
    public void moveToIsolation(User user) {
        // 격리 테이블로 옮기기
        userIsolationRepository.insertUserIntoIsolation(user.getId());
    }



    // 휴면계정 아이디 비밀번호 매칭
    @Transactional
    public boolean validatePasswordAtIsolationTable(String loginId, String password) {
        return bCryptPasswordEncoder.matches(password, userIsolationRepository.findByLoginId(loginId).get().getPassword());
    }

    @Transactional
    public User sleepingUserLogin(UserDto.LoginForm loginForm) {
        //격리 테이블에 있으면
        if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isPresent()) {

            // 로그인 아이디로 격리 테이블 객체로 뽑아오기
            UserIsolation userIsolation = userIsolationService.loadUserFromLoginId(loginForm.getLoginId());

            // 블랙리스트 유저인지 확인 --> 블랙리스트면 에러
            blackListService.isBlackList(userIsolation.getEmail());

            // 정지 유저인지 확인
            userIsolationService.isRestricted(loginForm.getLoginId());

            //아이디 비밀번호 검증
            if (validatePasswordAtIsolationTable(loginForm.getLoginId(), loginForm.getPassword())) {

                // 격리 테이블 객체 정보를 본 테이블로 이동
                userRepository.insertUserIsolationIntoUser(userIsolation.getUserIdx());

                userIsolationRepository.deleteByLoginId(loginForm.getLoginId());
            } else {
                throw new AccountException(ErrorType.PASSWORD_ERROR);
            }
        }

        return userService.loadUserFromLoginId(loginForm.getLoginId());
    }

    // 휴면 계정 전환 30일 전 안내 메일 보내기
    // 테스트 환경 -> 휴면 계정 전환 5분 전에 메일 보냄
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void sendEmailSoonDormant() {

        // 마지막 로그인 일자가 지금으로부터 11달 전인 유저에게
        LocalDateTime targetTime = LocalDateTime.now().minusMonths(11);
//        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(5);

        // 휴면계정 전환 30일 전인 유저 목록 가져오기
        List<User> user = userRepository.findByLastLoginBefore(targetTime);

        // 대상 유저들에 이메일 보내기
        for (int i = 0; i < user.toArray().length; i++) {
            emailSender.send(user.get(i).getEmail(), buildSoonDormantTargetFormService.buildEmail());
        }
    }

    // 휴면계정 전환 --> 1년 이상 접속하지 않으면 휴면계정임
    // 테스트 환경 -> 10분 미 접속 시 휴면계정으로 전환
    @Transactional
    // @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 * * * * *")
    public void convertDormant() {

//        LocalDateTime targetTime = LocalDateTime.now().minusMonths(12);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(10);

        // 1년이상 접속하지 않은 유저 리스트 불러오기
        List<User> targetUser = userRepository.findByLastLoginBefore(targetTime);

        // 해당 유저들 격리테이블로 이동 후, 본 테이블에서 삭제
        for (int i = 0; i < targetUser.toArray().length; i++) {
            moveToIsolation(targetUser.get(i));
            userRepository.deleteById(targetUser.get(i).getId());
        }
    }

    // 휴면계정 전환 후 3년간 로그인 하지 않는 대상에 지정되기 한달 전에 이메일 보내기
    // 테스트 환경 -> 20분 미 접속 시 자동 삭제 대상 메일 전송
    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 * * * * *")
    public void autoDeleteTargetIsThreeYearsSendEmail() {
        LocalDateTime targetTime = LocalDateTime.now().minusYears(3).plusDays(30);
//        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(8);
        List<UserIsolation> targetUser = userIsolationRepository.findByLastLoginBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {
            emailSender.send(targetUser.get(i).getEmail(), buildAutoDeletedWarningUserFormService.buildEmail());
        }
    }

    // 휴면계정 전환 후 3년간 로그인 하지 않으면 계정 자동 삭제
    // 테스트 환경 -> 35분 미 접속 시 자동 삭제
    // 테스트 환경2 -> 30분 미 접속 시 자동 삭제
    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 * * * * *")
    public void autoDeleteTargetIsThreeYears() {
//        LocalDateTime targetTime = LocalDateTime.now().minusYears(3);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(30);
        List<UserIsolation> targetUser = userIsolationRepository.findByLastLoginBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {

            // 회원탈퇴 요청한 유저의 강의평가 삭제
            evaluatePostsService.deleteByUser(targetUser.get(i).getUserIdx());

            // 회원탈퇴 요청한 유저의 시험정보 삭제
            examPostsService.deleteByUser(targetUser.get(i).getUserIdx());

            // 유저 삭제
            userIsolationRepository.deleteByLoginId(targetUser.get(i).getLoginId());
        }
    }
}