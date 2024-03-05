package usw.suwiki.infra.caffeine.log;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;
import usw.suwiki.statistics.aspect.CacheStaticsLogger;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
class CaffeineCacheStaticsLogger implements CacheStaticsLogger {
    private final CacheManager cacheManager;

    public void getCachesStats(String cacheKeys) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(cacheKeys);
        CacheStats stats = Objects.requireNonNull(cache).getNativeCache().stats();

        log.info("Cache hit count: " + stats.hitCount());
        log.info("Cache miss count: " + stats.missCount());
    }
}
