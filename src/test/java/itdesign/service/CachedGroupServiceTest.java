package itdesign.service;

import itdesign.App;
import itdesign.entity.Group;
import itdesign.repo.GroupRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import static itdesign.helper.EntitiesHelper.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, properties = "spring.profiles.active=test")
public class CachedGroupServiceTest {
    @Autowired
    private CachedGroupService service;

    @MockBean
    private GroupRepo mockRepo;

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
