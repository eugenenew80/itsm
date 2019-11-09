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
    private Cache<Long, Status> cache = null;

    @Override
    public Status getStatus(Long statusId) {
        cache = (cache == null) ? ehcacheManager.getCache("statusCache", Long.class, Status.class) : cache;

        Status status = cache.get(statusId);
        if (status != null) {
            logger.debug("status from cache, statusId: " + statusId);
            return status;
        }

        logger.debug("status from db, statusId: " + statusId);
        status = repo.findOne(statusId);
        cache.putIfAbsent(statusId, status);
        return status;
    }
}
