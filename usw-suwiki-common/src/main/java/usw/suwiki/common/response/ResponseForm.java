package usw.suwiki.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseForm {

    private Object data;
    private Integer statusCode;
    private String message;

    public ResponseForm(Object data) {
        this.data = data;
    }

    public static ResponseForm success(Object data) {
        return new ResponseForm(data, 200, "Success");
    }

    public static ResponseForm buildDataStatusCodeForm(Object data, Integer statusCode) {
        return new ResponseForm(data, statusCode, "");
    }

    public ResponseForm buildDataStatusCodeMessageForm(
        Object data,
        Integer statusCode,
        String message
    ) {
        return new ResponseForm(data, statusCode, message);
    }
}
