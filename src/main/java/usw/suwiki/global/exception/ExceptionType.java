package usw.suwiki.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionType {

    /**
     * SERVER ERROR
     */
    SERVER_ERROR("SERVER001", "서버 오류 입니다. 관리자에게 문의해주세요", INTERNAL_SERVER_ERROR),

    /**
     * Domain : User
     */
    IS_NOT_EMAIL_FORM("USER003", "올바른 이메일 형식이 아닙니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS("USER004", "사용자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_ERROR("USER005", "비밀번호를 확인해주세요.", HttpStatus.BAD_REQUEST),
    USER_POINT_LACK("USER006", "유저 포인트가 부족합니다.", HttpStatus.BAD_REQUEST),
    LOGIN_REQUIRED("USER007", "로그인이 필요합니다.", FORBIDDEN),
    USER_RESTRICTED("USER008", "접근 권한이 없는 사용자 입니다. 관리자에게 문의하세요.", FORBIDDEN),
    YOU_ARE_IN_BLACKLIST("USER009", "블랙리스트 대상입니다. 이용할 수 없습니다.", FORBIDDEN),
    LOGIN_ID_OR_EMAIL_OVERLAP("USER010", "아이디 혹은 이메일이 중복됩니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_CHANGED("USER011", "이전 비밀번호와 동일하게 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),

    /**
     * Domain : ConfirmationToken
     */
    EMAIL_NOT_AUTHED("CONFIRMATION_TOKEN001", "이메일 인증을 받지 않은 사용자 입니다.", UNAUTHORIZED),
    EMAIL_VALIDATED_ERROR("CONFIRMATION_TOKEN002", "이메일 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_VALIDATED_ERROR_RETRY("CONFIRMATION_TOKEN003", "이메일 인증 만료기간이 지나거나, 예기치 못한 오류로 이메일 인증에 실패했습니다. 회원가입을 다시 진행해주세요", HttpStatus.BAD_REQUEST),
    EMAIL_AUTH_TOKEN_ALREADY_USED("CONFIRMATION_TOKEN004", "이미 사용된 인증 토큰 입니다.", HttpStatus.BAD_REQUEST),

    /**
     * Domain : ExamPost
     */
    POSTS_WRITE_OVERLAP("POSTS001", "이미 작성한 정보입니다.", HttpStatus.BAD_REQUEST),
    EXAM_POST_NOT_FOUND("EXAM_POST001", "해당 시험정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),


    /**
     * Domain : EvaluatePost
     */


    /**
     * Domain : BlackListDomain
     */
    USER_ALREADY_BLACKLISTED("USER014", "이미 블랙리스트인 사용자 입니다.", HttpStatus.BAD_REQUEST),
    USER_IS_BLACKLISTED("USER015", "신고 당한 횟수 3회 누적으로 블랙리스트 조치 되었습니다. 더 이상 서비스를 이용할 수 없습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER013", "해당 이메일에 대한 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    /**
     * Domain : Token
     */
    TOKEN_IS_NOT_FOUND("TOKEN001", "토큰이 만료되었거나, 유효하지 않습니다. 다시 로그인 해주세요", UNAUTHORIZED),

    /**
     * Domain : Lecture
     */
    //400
    INVALID_ORDER_OPTION("OPTION001", "ORDER OPTION을 확인해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_OPTION("OPTION002", "MAJOR OPTION을 확인해주세요.", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_LECTURE_NAME("LECTURE001", "강의 제목을 입력해주세요", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_PROFESSOR_NAME("LECTURE002", "교수 이름을 입력해주세요", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_LECTURE("LECTURE003", "해당 강의가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    //404
    LECTURE_NOT_FOUND("LECTURE001", "해당 강의에 대한 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),


    /**
     * Domain : Notice
     */
    NOTICE_NOT_FOUND("NOTICE001", "해당 공지사항을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),


    /**
     * 공통
     */
    PARAM_VALID_ERROR("PARAM001", "Exception Message", HttpStatus.BAD_REQUEST),
    SEND_MAIL_FAILED("MAIL001", "메일 전송에 실패했습니다.", INTERNAL_SERVER_ERROR), //500
    METHOD_NOT_ALLOWED("METHOD001", "Exception Message", HttpStatus.METHOD_NOT_ALLOWED),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

}