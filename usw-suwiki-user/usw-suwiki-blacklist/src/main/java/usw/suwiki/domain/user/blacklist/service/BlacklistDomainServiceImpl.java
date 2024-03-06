package usw.suwiki.domain.user.blacklist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.secure.PasswordEncoder;
import usw.suwiki.domain.user.blacklist.BlacklistDomain;
import usw.suwiki.domain.user.blacklist.BlacklistRepository;
import usw.suwiki.domain.user.service.BlacklistDomainService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
class BlacklistDomainServiceImpl implements BlacklistDomainService {

    private final BlacklistRepository blacklistRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void isUserInBlackListThatRequestJoin(String email) {
        List<BlacklistDomain> blacklist = blacklistRepository.findAll();
        for (BlacklistDomain blackListUser : blacklist) {
            if (passwordEncoder.matches(email, blackListUser.getHashedEmail())) {
                throw new AccountException(ExceptionType.YOU_ARE_IN_BLACKLIST);
            }
        }
    }
}
