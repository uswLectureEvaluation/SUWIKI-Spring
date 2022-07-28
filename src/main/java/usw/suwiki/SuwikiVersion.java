package usw.suwiki;

import jdk.jshell.execution.Util;
import lombok.Getter;

/**
 * effective java item4 적용.
 */
@Getter
public class SuwikiVersion {
    public static final float version = 1;

    //never instantiated
    private SuwikiVersion() {
    }
}
