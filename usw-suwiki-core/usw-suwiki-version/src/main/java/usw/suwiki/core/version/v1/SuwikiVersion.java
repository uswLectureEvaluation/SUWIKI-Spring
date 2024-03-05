package usw.suwiki.core.version.v1;

import lombok.Getter;

@Getter
public class SuwikiVersion {

    public static final float version = 1.01f;

    private SuwikiVersion() {
        throw new IllegalStateException("never instantiated");
    }
}
