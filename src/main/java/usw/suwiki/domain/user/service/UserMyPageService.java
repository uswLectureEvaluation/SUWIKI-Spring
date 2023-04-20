package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserResponseDto.MyPageForm;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMyPageService {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserService userService;

    public MyPageForm execute(String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        Long userIdx = jwtTokenResolver.getId(Authorization);
        User user = userService.loadUserFromUserIdx(userIdx);
        return MyPageForm.builder()
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .point(user.getPoint())
                .writtenEvaluation(user.getWrittenEvaluation())
                .writtenExam(user.getWrittenExam())
                .viewExam(user.getViewExamCount())
                .build();
    }
}
