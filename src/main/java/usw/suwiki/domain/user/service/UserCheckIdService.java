package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserRequestDto.CheckLoginIdForm;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.userIsolation.repository.UserIsolationRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCheckIdService {

    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;

    public Map<String, Boolean> execute(CheckLoginIdForm checkLoginIdForm) {

    }
}
