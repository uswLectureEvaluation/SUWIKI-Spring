package usw.suwiki.global;

import lombok.Getter;

@Getter
public enum CacheType {
    LECTURE("lecture", 60, 1000);

    CacheType(String cacheName, int expiredAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize;
    }

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
