package usw.suwiki.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // Internal Serer Error
    SERVER_ERROR("SERVER001", "서버 오류 입니다. 관리자에게 문의해주세요", HttpStatus.INTERNAL_SERVER_ERROR),

    // 401 Error 이메일 인증을 받지 않았을때
    USER_NOT_EMAIL_AUTHED("USER016", "이메일 인증을 받지 않은 사용자 입니다.", HttpStatus.UNAUTHORIZED),

    // User 400 Error
    USER_ID_EXISTS("USER001", "아이디가 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS("USER002", "이메일이 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    USER_JOIN_FAILED("USER003", "회원 가입에 실패 했습니다. 관리자에게 문의하세요", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS("USER004", "사용자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_ERROR("USER005", "비밀번호를 확인해주세요.", HttpStatus.BAD_REQUEST),
    EMAIL_VALIDATED_ERROR("USER006", "이메일 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_AUTH_TOKEN_ALREADY_USED("USER007", "이미 사용된 인증 토큰 입니다.", HttpStatus.BAD_REQUEST),
    IS_NOT_EMAIL_FORM("USER007", "올바른 이메일 형식이 아닙니다.", HttpStatus.BAD_REQUEST),
    USER_AND_EMAIL_NOT_EXISTS_AND_AUTH("USER0010", "아이디 혹은 이메일이 존재하지 않거나, 이메일 인증을 받지 않은 사용자 입니다.", HttpStatus.BAD_REQUEST),
    USER_RESTRICTED("USER011", "접근 권한이 없는 사용자 입니다. 관리자에게 문의하세요.", HttpStatus.FORBIDDEN), //403
    USER_AND_EMAIL_OVERLAP("USER012", "아이디 혹은 이메일이 중복됩니다.", HttpStatus.BAD_REQUEST),

    YOU_ARE_IN_BLACKLIST("USER016", "블랙리스트 대상입니다. 이용할 수 없습니다.", HttpStatus.FORBIDDEN),
    USER_ALREADY_BLACKLISTED("USER014", "이미 블랙리스트인 사용자 입니다.", HttpStatus.BAD_REQUEST),
    USER_IS_BLACKLISTED("USER015", "신고 당한 횟수 3회 누적으로 블랙리스트 조치 되었습니다. 더 이상 서비스를 이용할 수 없습니다.", HttpStatus.BAD_REQUEST),

    BAD_REQUEST("USER013", "요청이 잘못 되었습니다.", HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND("USER013", "해당 이메일에 대한 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // JWT 401 Error
    TOKEN_IS_NOT_FOUND("SECURITY005", "토큰이 만료되었거나, 유효하지 않습니다. 다시 로그인 해주세요", HttpStatus.UNAUTHORIZED),

    //Post 400 error
    POSTS_WRITE_OVERLAP("POSTS001", "이미 작성한 정보입니다.", HttpStatus.BAD_REQUEST),

    //Buy User Point error
    USER_POINT_LACK("POINTS001", "유저 포인트가 부족합니다.", HttpStatus.BAD_REQUEST),

    //Option error
    INVALID_ORDER_OPTION("OPTION001", "ORDER OPTION을 확인해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_OPTION("OPTION002", "MAJOR OPTION을 확인해주세요.", HttpStatus.BAD_REQUEST),

    //Lecture 400 error
    NOT_EXISTS_LECTURE_NAME("LECTURE001", "강의 제목을 입력해주세요", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_PROFESSOR_NAME("LECTURE002", "교수 이름을 입력해주세요", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_LECTURE("LECTURE003", "해당 강의가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),

    // Security 401 Error
    UNAUTHENTICATED("SECURITY001", "로그인이 필요한 기능입니다.", HttpStatus.UNAUTHORIZED), //401
    LOGIN_FAILED("SECURITY003", "로그인에 실패했습니다. ID, PASSWORD 를 확인해주세요.", HttpStatus.UNAUTHORIZED),
    LOGIN_REQUIRED("SECURITY004", "세션이 만료되었습니다. 다시 로그인 하세요.", HttpStatus.UNAUTHORIZED),

    // Param
    PARAM_VALID_ERROR("PARAM001", "Exception Message", HttpStatus.BAD_REQUEST),

    // Mail
    SEND_MAIL_FAILED("MAIL001", "메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR), //500

    // METHOD
    METHOD_NOT_ALLOWED("METHOD001", "Exception Message", HttpStatus.METHOD_NOT_ALLOWED); // 405

    private final String code;
    private final String message;
    private final HttpStatus status;

}