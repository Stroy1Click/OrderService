package ru.stroy1click.order.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheClear {

    private final CacheManager cacheManager;

    public void clearOrdersByUserId(Long userId){
        log.info("clearOrdersByUserId {}", userId);
        deleteCache("ordersByUserId", userId);
    }

    private void deleteCache(String key, Object value){
        Cache cache = this.cacheManager.getCache(key);
        if(cache != null){
            cache.evict(value);
        }
    }
}