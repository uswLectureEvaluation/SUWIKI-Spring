package usw.suwiki.domain.blacklistdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlacklistDomain;
import usw.suwiki.domain.blacklistdomain.repository.BlacklistRepository;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import java.util.List;

import static usw.suwiki.global.exception.ExceptionType.YOU_ARE_IN_BLACKLIST;


@Service
@RequiredArgsConstructor
@Transactional
public class BlacklistDomainService {

    private final BlacklistRepository blacklistRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void isUserInBlackListThatRequestJoin(String email) {
        List<BlacklistDomain> blacklist = blacklistRepository.findAll();
        for (BlacklistDomain blackListUser : blacklist) {
            if (bCryptPasswordEncoder.matches(email, blackListUser.getHashedEmail())) {
                throw new AccountException(YOU_ARE_IN_BLACKLIST);
            }
        }
    }
}
