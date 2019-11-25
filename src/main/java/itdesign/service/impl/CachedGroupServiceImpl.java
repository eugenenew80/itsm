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
    private Cache<String, Group> cache = null;

    @Override
    public Group getGroup(String groupCode, String lang) {
        cache = (cache == null) ? ehcacheManager.getCache("groupCache", String.class, Group.class) : cache;

        String key = groupCode + "#" + lang;
        Group group = cache.get(key);
        if (group != null) {
            logger.trace("group from cache, key: " + key);
            return group;
        }

        logger.trace("group from db, key: " + key);
        group = repo.findByCodeAndLang(groupCode, lang);
        if (group != null)
            cache.putIfAbsent(key, group);

        return group;
    }
}
