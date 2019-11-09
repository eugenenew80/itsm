package itdesign.service;

import itdesign.App;
import itdesign.entity.Group;
import itdesign.repo.GroupRepo;
import itdesign.service.impl.CachedGroupServiceImpl;
import org.ehcache.CacheManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static itdesign.helper.EntitiesHelper.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, properties = "spring.profiles.active=test")
public class CachedGroupServiceTest {
    private GroupRepo mockRepo;
    private CachedGroupService service;

    @Autowired
    private  CacheManager ehcacheManager;

    @Before
    public void setUp() {
        mockRepo = mock(GroupRepo.class);
        service = new CachedGroupServiceImpl(mockRepo, ehcacheManager);
    }

    @Test
    public void statusShouldBeCached() {
        long testedGroupId = 1l;
        when(mockRepo.findOne(testedGroupId)).thenReturn(newGroup(testedGroupId));

        Group group = service.getGroup(testedGroupId);
        verify(mockRepo, times(1)).findOne(testedGroupId);
        assertGroup(group);

        group = service.getGroup(testedGroupId);
        verify(mockRepo, times(1)).findOne(testedGroupId);
        assertGroup(group);
    }
}
