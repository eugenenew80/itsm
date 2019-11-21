package itdesign.service;

import itdesign.entity.Group;

public interface CachedGroupService {
    Group getGroup(String groupCode, String lang);
}
