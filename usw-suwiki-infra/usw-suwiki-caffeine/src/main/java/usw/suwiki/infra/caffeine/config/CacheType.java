package usw.suwiki.infra.caffeine.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum CacheType {
    LECTURE("lecture", 10 * 60, 1000);

    private final String cacheName;
    private final Integer expiredAfterWrite;
    private final Integer maximumSize;
}
