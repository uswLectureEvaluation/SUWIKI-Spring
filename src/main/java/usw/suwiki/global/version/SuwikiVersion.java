package usw.suwiki.global.version;

import lombok.Getter;

@Getter
public class SuwikiVersion {

    public static final float version = 1.01f;

    //never instantiated
    private SuwikiVersion() {
    }
}
