package usw.suwiki.domain.user.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.service.EmailSendService;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.util.emailBuild.BuildPersonalInformationUsingNotifyForm;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserCrontabService {

    private final UserRepository userRepository;
    private final BuildPersonalInformationUsingNotifyForm buildPersonalInformationUsingNotifyForm;
    private final EmailSendService emailSendService;

    @Scheduled(cron = "8 0 0 1 1 *")
    public void sendPrivacyPolicyMail() {
        List<User> users = userRepository.findAll();
        String emailContent = buildPersonalInformationUsingNotifyForm.buildEmail();
        for (User user : users) {
            emailSendService.send(user.getEmail(), emailContent);
        }
    }
}
