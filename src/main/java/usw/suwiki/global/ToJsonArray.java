package usw.suwiki.global;

import lombok.Getter;

@Getter
public class ToJsonArray {
    Object data;

    public ToJsonArray(Object data) {
        this.data = data;
    }
}
