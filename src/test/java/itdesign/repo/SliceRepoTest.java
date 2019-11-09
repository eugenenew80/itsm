package itdesign.repo;

import itdesign.App;
import itdesign.entity.Slice;
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

import static itdesign.helper.EntitiesHelper.assertSlice;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, properties = "spring.profiles.active=test")
public class SliceRepoTest {

    @Autowired
    private SliceRepo repo;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        List<DataSetLoader> loaders = Arrays.asList(
            new DataSetLoader("slice", "slices.xml"),
            new DataSetLoader("slice", "rep_groups.xml"),
            new DataSetLoader("slice", "rep_statuses.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.deleteAll(dataSource.getConnection());

        loaders = Arrays.asList(
            new DataSetLoader("slice", "rep_statuses.xml"),
            new DataSetLoader("slice", "rep_groups.xml"),
            new DataSetLoader("slice", "slices.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.cleanAndInsert(dataSource.getConnection());
    }

    @Test
    public void listStatusesMayBeFind()  {
        List<Slice> list = repo.findAll();
        assertThat(list, is(not(empty())));
    }

    @Test
    public void existingStatusMayBeFindById()  {
        Slice entity = repo.findOne(1l);
        assertSlice(entity);
    }
}
