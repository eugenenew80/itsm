package itdesign.repo;

import itdesign.App;
import itdesign.entity.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, properties = "spring.profiles.active=dev")
public class GroupRepoTest {

    @Autowired
    private GroupRepo repo;

    @Test
    public void whenFindById_thenReturnLogPoint() {
        Group group = repo.findOne(1l);
        assertThat(group.getId()).isEqualTo(1l);
    }
}
