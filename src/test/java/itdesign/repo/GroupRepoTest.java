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
import static itdesign.helper.EntitiesHelper.newGroup;
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
    public void listGroupsMayBeFound()  {
        List<Group> groups = repo.findAll();
        assertThat(groups, is(not(empty())));
    }

    @Test
    public void existingGroupMayBeFoundById()  {
        long testedGroupId = 1l;
        Group group = repo.findOne(testedGroupId);
        assertGroup(group);
    }

    @Test
    public void groupShouldBeImmutable() {
        long testedGroupId = 1l;
        Group group = repo.findOne(testedGroupId);
        String groupName = group.getName();

        group.setName("New group 1");
        repo.save(group);

        Group savedGroup = repo.findOne(testedGroupId);
        assertThat(savedGroup.getName(), equalTo(groupName));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldFailWhenTryCreateGroup() {
        repo.save(newGroup(null));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldFailWhenTryRemoveGroup() {
        long testedGroupId = 1l;
        repo.delete(testedGroupId);
    }
}
