package usw.suwiki.domain.userIsolation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.EmailSender;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.global.util.emailBuild.BuildAutoDeletedWarningUserFormService;


@Service
@RequiredArgsConstructor
public class UserIsolationService {

    private final UserIsolationRepository userIsolationRepository;


    private final EmailSender emailSender;
    private final BuildAutoDeletedWarningUserFormService buildAutoDeletedWarningUserFormService;

    // loginId로 유저 격리 테이블 꺼내오기
    @Transactional
    public UserIsolation loadUserFromLoginId(String loginId) {
        return userIsolationRepository.findByLoginId(loginId).orElseThrow(() -> new AccountException(ErrorType.USER_NOT_EXISTS));
    }
}
