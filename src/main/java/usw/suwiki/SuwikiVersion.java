package usw.suwiki;

import lombok.Getter;

/**
 * effective java item4 적용.
 */
@Getter
public class SuwikiVersion {

    public static final float version = 1.01f;

    //never instantiated
    private SuwikiVersion() {
    }
}
