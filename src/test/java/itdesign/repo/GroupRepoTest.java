package itdesign.repo;

import itdesign.App;
import itdesign.entity.Group;
import itdesign.helper.DataSetLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static itdesign.helper.EntitiesHelper.assertGroup;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, properties = "spring.profiles.active=test")
public class GroupRepoTest {

    @Autowired
    private GroupRepo repo;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        List<DataSetLoader> loaders = Arrays.asList(
            new DataSetLoader("slice", "slices.xml"),
            new DataSetLoader("slice", "rep_groups.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.deleteAll(dataSource.getConnection());

        loaders = Arrays.asList(
            new DataSetLoader("slice", "rep_groups.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.cleanAndInsert(dataSource.getConnection());
    }

    @Test
    public void listGroupsMayBeFind()  {
        List<Group> groups = repo.findAll();
        assertThat(groups, is(not(empty())));
    }

    @Test
    public void existingRoleMayBeFindById()  {
        Group group = repo.findOne(1l);
        assertGroup(group);
    }
}
