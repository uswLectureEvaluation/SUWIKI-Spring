package usw.suwiki.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseForm {

    Object data;
    String statusCode;
    String message;

    public ResponseForm(Object data) {
        this.data = data;
    }
}
