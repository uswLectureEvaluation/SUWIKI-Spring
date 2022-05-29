package usw.suwiki.global;

import lombok.Getter;

import java.util.Optional;

@Getter
public class ToJsonArray {
    Object data;

    public ToJsonArray(Object data) {
        this.data = data;
    }
}
