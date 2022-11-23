package usw.suwiki.domain.userIsolation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.service.EmailSender;
import usw.suwiki.domain.userIsolation.entity.UserIsolation;
import usw.suwiki.domain.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.exception.errortype.AccountException;
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
