package itdesign.repo;

import itdesign.App;
import itdesign.entity.Status;
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

import static itdesign.helper.EntitiesHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, properties = "spring.profiles.active=test")
public class StatusRepoTest {

    @Autowired
    private StatusRepo repo;

    @Autowired
    private  DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        List<DataSetLoader> loaders = Arrays.asList(
            new DataSetLoader("slice", "slices.xml"),
            new DataSetLoader("slice", "rep_statuses.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.deleteAll(dataSource.getConnection());

        loaders = Arrays.asList(
            new DataSetLoader("slice", "rep_statuses.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.cleanAndInsert(dataSource.getConnection());
    }

    @Test
    public void listStatusesMayBeFound()  {
        List<Status> list = repo.findAll();
        assertThat(list, is(not(empty())));
    }

    @Test
    public void existingStatusMayBeFoundById()  {
        long testedStatusId = 1l;
        Status entity = repo.findOne(testedStatusId);
        assertStatus(entity);
        assertThat(entity.getId(), is(equalTo(testedStatusId)));
    }


    @Test
    public void newStatusMayBeCreated() {
        Status status = newStatus(null);

        repo.save(status);
        assertThat(status.getId(), is(not(equalTo(null))));
        assertThat(status.getId(), is(greaterThan(0L)));

        Status findStatus = repo.findOne(status.getId());
        assertStatus(findStatus);
    }

    @Test
    public void statusShouldBeImmutable() {
        long testedStatusId = 1l;
        Status status = repo.findOne(testedStatusId);
        String statusName = status.getName();

        status.setName("New status 1");
        repo.save(status);

        Status savedStatus = repo.findOne(testedStatusId);
        assertThat(savedStatus.getName(), equalTo(statusName));
    }

    /*
    @Test(expected=UnsupportedOperationException.class)
    public void shouldFailWhenTryRemoveStatus() {
        long testedStatusId = 1l;
        repo.delete(testedStatusId);
    }
    */
}
