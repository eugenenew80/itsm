package itdesign.service;

import itdesign.App;
import itdesign.entity.Status;
import itdesign.repo.StatusRepo;
import itdesign.service.impl.CashedStatusServiceImpl;
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
public class CachedStatusServiceTest {
    private StatusRepo mockRepo;
    private CachedStatusService service;

    @Autowired
    private  CacheManager ehcacheManager;

    @Before
    public void setUp() {
        mockRepo = mock(StatusRepo.class);
        service = new CashedStatusServiceImpl(mockRepo, ehcacheManager);
    }

    @Test
    public void statusShouldBeCached() {
        long testedStatusId = 1l;
        when(mockRepo.findOne(testedStatusId)).thenReturn(newStatus(testedStatusId));

        Status status = service.getStatus(testedStatusId);
        verify(mockRepo, times(1)).findOne(testedStatusId);
        assertStatus(status);

        status = service.getStatus(testedStatusId);
        verify(mockRepo, times(1)).findOne(testedStatusId);
        assertStatus(status);
    }
}
