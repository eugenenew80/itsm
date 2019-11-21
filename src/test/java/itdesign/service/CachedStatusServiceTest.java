package itdesign.service;

import itdesign.App;
import itdesign.entity.Status;
import itdesign.helper.EntitiesHelper;
import itdesign.repo.StatusRepo;
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
public class CachedStatusServiceTest {

    @MockBean
    private StatusRepo mockRepo;

    @Autowired
    private CachedStatusService service;

    @Test
    public void statusShouldBeCached() {
        long testedStatusId = 1l;
        when(mockRepo.findOne(testedStatusId)).thenReturn(newStatus(testedStatusId));

        Status status = service.getStatus(EntitiesHelper.STATUS_CODE, "RU");
        verify(mockRepo, times(1)).findOne(testedStatusId);
        assertStatus(status);

        status = service.getStatus(EntitiesHelper.STATUS_CODE, "RU");
        verify(mockRepo, times(1)).findOne(testedStatusId);
        assertStatus(status);
    }
}
