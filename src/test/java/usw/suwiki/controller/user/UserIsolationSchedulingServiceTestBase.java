package usw.suwiki.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.IntegrationTestBase;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenCRUDService;
import usw.suwiki.domain.evaluatepost.controller.dto.EvaluatePostSaveDto;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostCRUDService;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.domain.exampost.service.ExamPostCRUDService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.refreshtoken.service.RefreshTokenCRUDService;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.domain.user.userIsolation.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.user.userIsolation.service.UserIsolationCRUDService;
import usw.suwiki.domain.userlecture.viewexam.service.ViewExamCRUDService;
import usw.suwiki.global.util.emailBuild.BuildSoonDormantTargetForm;
import usw.suwiki.global.util.emailBuild.UserAutoDeletedWarningForm;
import usw.suwiki.global.util.mailsender.EmailSender;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserIsolationSchedulingServiceTestBase extends IntegrationTestBase {

    @Autowired
    UserCRUDService userCRUDService;
    @Autowired
    UserIsolationCRUDService userIsolationCRUDService;
    @Autowired
    BuildSoonDormantTargetForm buildSoonDormantTargetForm;
    @Autowired
    EmailSender emailSender;
    @Autowired
    EvaluatePostService evaluatePostService;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    UserIsolationRepository userIsolationRepository;
    @Autowired
    ViewExamCRUDService viewExamCRUDService;
    @Autowired
    RefreshTokenCRUDService refreshTokenCRUDService;
    @Autowired
    ReportPostService reportPostService;
    @Autowired
    EvaluatePostCRUDService evaluatePostCRUDService;
    @Autowired
    ExamPostCRUDService examPostCRUDService;
    @Autowired
    FavoriteMajorService favoriteMajorService;
    @Autowired
    RestrictingUserService restrictingUserService;
    @Autowired
    ConfirmationTokenCRUDService confirmationTokenCRUDService;

    @Autowired
    UserAutoDeletedWarningForm userAutoDeletedWarningForm;

    @BeforeEach
    public void setup() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    conn,
                    new ClassPathResource("/data/insert-lecture.sql")
            );
            ScriptUtils.executeSqlScript(
                    conn,
                    new ClassPathResource("/data/insert-user.sql")
            );
        }

        EvaluatePostSaveDto evaluatePostSaveDto = EvaluatePostSaveDto.builder()
                .lectureName("테스트 과목입니다.")
                .selectedSemester("테스트 학기")
                .professor("테스트 교수")
                .satisfaction(5.0F)
                .learning(5.0F)
                .honey(5.0F)
                .team(1)
                .difficulty(1)
                .homework(1)
                .content("테스트 게시글 내용")
                .build();

        evaluatePostService.write(evaluatePostSaveDto, 5L, 1L);
        evaluatePostService.write(evaluatePostSaveDto, 6L, 1L);

    }

    @DisplayName("휴면 계정 전환 한 달전 유저들에게 이메일 보내기 테스트")
    @Test
    public void sendEmailAboutSleeping() {
        LocalDateTime startTime = LocalDateTime.now().minusMonths(12);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(11);
        List<User> users = userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime);
        for (User user : users) {
            emailSender.send(user.getEmail(), buildSoonDormantTargetForm.buildEmail());
        }
    }

    @DisplayName("휴면 계정으로 전환될 대상은 두 명, 그리고 성공적으로 전환되었는지 확인한다.")
    @Test
    public void convertSleepingTable() {
        LocalDateTime startTime = LocalDateTime.now().minusMonths(35);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(12);
        List<User> users = userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime);
        for (User user : users) {
            UserIsolation userIsolation = UserIsolation.builder()
                    .userIdx(user.getId())
                    .loginId(user.getLoginId())
                    .password(user.getPassword())
                    .email(user.getEmail())
                    .lastLogin(user.getLastLogin())
                    .requestedQuitDate(user.getRequestedQuitDate())
                    .build();
            userIsolationCRUDService.saveUserIsolation(userIsolation);
            userCRUDService.softDeleteForIsolation(user.getId());
        }
        List<UserIsolation> all = userIsolationRepository.findAll();
        for (UserIsolation userIsolation : all) {
            System.out.println("userIsolation.getLoginId() = " + userIsolation.getLoginId());
        }

        assertThat(userIsolationRepository.findAll().size()).isEqualTo(2);
        assertThat(userCRUDService.loadUserFromUserIdx(7L).getEmail()).isNull();
        assertThat(userCRUDService.loadUserFromUserIdx(8L).getEmail()).isNull();
    }

    @DisplayName("계정 자동 삭제 안내 기간은 (3년 - 1달), 그리고 2명이 그 대상이다.")
    @Test
    public void sendEmailAutoDeleteTargeted() {
        LocalDateTime startTime = LocalDateTime.now().minusMonths(36);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(35);
        List<User> users = userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime);
        int count = 0;
        for (User user : users) {
            count++;
            emailSender.send(user.getEmail(), userAutoDeletedWarningForm.buildEmail());
        }
        assertThat(count).isEqualTo(2);
    }

    @DisplayName("계정 자동 삭제 기간은 3년, 그리고 2명이 그 대상이다.")
    @Test
    public void autoDeleteTargetIsThreeYears() {
        LocalDateTime startTime = LocalDateTime.now().minusMonths(100);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(36);
        List<User> users = userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime);
        int count = 0;
        for (User user : users) {
            count++;
            Long userIdx = user.getId();
            viewExamCRUDService.deleteAllFromUserIdx(userIdx);
            refreshTokenCRUDService.deleteFromUserIdx(userIdx);
            reportPostService.deleteFromUserIdx(userIdx);
            evaluatePostCRUDService.deleteFromUserIdx(userIdx);
            examPostCRUDService.deleteFromUserIdx(userIdx);
            favoriteMajorService.deleteFromUserIdx(userIdx);
            restrictingUserService.deleteFromUserIdx(userIdx);
            confirmationTokenCRUDService.deleteFromUserIdx(userIdx);
            userIsolationCRUDService.deleteByUserIdx(userIdx);
            userCRUDService.deleteFromUserIdx(userIdx);
        }
        assertThat(count).isEqualTo(2);
    }
}
