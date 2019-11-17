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
import static itdesign.helper.EntitiesHelper.newSlice;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
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
    public void listSlicesMayBeFound() {
        List<Slice> list = repo.findAll();
        assertThat(list, is(not(empty())));
    }

    @Test
    public void existingSliceMayBeFoundById() {
        long testedSliceId = 1l;
        Slice entity = repo.findOne(testedSliceId);
        assertSlice(entity);
        assertThat(entity.getId(), is(equalTo(testedSliceId)));
    }

    @Test
    public void newSliceMayBeCreated() {
        Slice slice = newSlice(null);

        repo.save(slice);
        assertThat(slice.getId(), is(not(equalTo(null))));
        assertThat(slice.getId(), is(greaterThan(0L)));

        Slice findSlice = repo.findOne(slice.getId());
        assertSlice(findSlice);
    }

    @Test
    public void inExistingSliceStatusMayBeChanged() {
        Long testedSliceId = 1l;
        Slice slice = repo.findOne(testedSliceId);

        String newStatusCode = "2";
        slice.setStatusCode(newStatusCode);
        repo.save(slice);

        Slice findSlice = repo.findOne(slice.getId());
        assertThat(findSlice.getStatusCode(), equalTo(newStatusCode));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldFailWhenTryRemoveSlice() {
        long testedSliceId = 1l;
        repo.delete(testedSliceId);
    }

}