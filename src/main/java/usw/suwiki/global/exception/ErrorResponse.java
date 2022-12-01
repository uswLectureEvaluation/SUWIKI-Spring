package usw.suwiki.global.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private String exception;

    private String code;

    private String message;

    private int status;

    private String error;

}