package usw.suwiki.global;

import lombok.Getter;

@Getter
public class ResponseForm {

    Object data;

    public ResponseForm(Object data) {
        this.data = data;
    }
}
