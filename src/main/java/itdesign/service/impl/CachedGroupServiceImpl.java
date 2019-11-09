package itdesign.service.impl;

import itdesign.entity.Group;
import itdesign.repo.GroupRepo;
import itdesign.service.CachedGroupService;
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
public class CachedGroupServiceImpl implements CachedGroupService {
    private static final Logger logger = LoggerFactory.getLogger(CachedGroupServiceImpl.class);
    private final GroupRepo repo;
    private final CacheManager ehcacheManager;
    private Cache<Long, Group> cache = null;

    @Override
    public Group getGroup(Long groupId) {
        cache = (cache == null) ? ehcacheManager.getCache("groupCache", Long.class, Group.class) : cache;

        Group group = cache.get(groupId);
        if (group != null) {
            logger.debug("group from cache, groupId: " + groupId);
            return group;
        }

        logger.debug("group from db, groupId: " + groupId);
        group = repo.findOne(groupId);
        cache.putIfAbsent(groupId, group);
        return group;
    }
}
