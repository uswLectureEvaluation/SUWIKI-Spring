package usw.suwiki.domain.user.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserResponseDto.MyPageResponse;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMyPageUseCase {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserService userService;

    public MyPageResponse execute(String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        Long userIdx = jwtTokenResolver.getId(Authorization);
        User user = userService.loadUserFromUserIdx(userIdx);
        return MyPageResponse.builder()
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .point(user.getPoint())
                .writtenEvaluation(user.getWrittenEvaluation())
                .writtenExam(user.getWrittenExam())
                .viewExam(user.getViewExamCount())
                .build();
    }
}
