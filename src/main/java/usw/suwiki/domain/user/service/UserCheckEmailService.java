package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserRequestDto.CheckEmailForm;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.userIsolation.repository.UserIsolationRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCheckEmailService {

    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;

    public Map<String, Boolean> execute(CheckEmailForm checkEmailForm) {
        if (userRepository.findByEmail(checkEmailForm.getEmail()).isPresent() ||
                userIsolationRepository.findByEmail(checkEmailForm.getEmail()).isPresent()) {
            return new HashMap<>() {{
                put("overlap", true);
            }};
        }
        return new HashMap<>() {{
            put("overlap", false);
        }};
    }
}
