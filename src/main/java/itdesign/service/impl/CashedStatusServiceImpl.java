package itdesign.service.impl;

import itdesign.entity.Status;
import itdesign.repo.StatusRepo;
import itdesign.service.CachedStatusService;
import lombok.RequiredArgsConstructor;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CashedStatusServiceImpl implements CachedStatusService {
    private static final Logger logger = LoggerFactory.getLogger(CashedStatusServiceImpl.class);
    private final StatusRepo repo;
    private final CacheManager ehcacheManager;
    private Cache<String, Status> cache = null;

    @Override
    public Status getStatus(String statusCode, String lang) {
        cache = (cache == null) ? ehcacheManager.getCache("statusCache", String.class, Status.class) : cache;

        String key = statusCode + "#" + lang;
        Status status = cache.get(key);
        if (status != null) {
            logger.debug("status from cache, key: " + key);
            return status;
        }

        logger.debug("status from db, key: " + key);
        status = repo.findByCodeAndLang(statusCode, key);
        cache.putIfAbsent(key, status);
        return status;
    }
}
