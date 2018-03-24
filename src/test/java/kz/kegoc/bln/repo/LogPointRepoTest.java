package kz.kegoc.bln.repo;

import kz.kegoc.bln.App;
import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.entity.Unit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class LogPointRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LogPointRepo logPointRepo;

    @Test
    public void whenFindByIdThenReturnLogPoint() {
        Unit unit = new Unit();
        unit.setName("Ед. изм. 1");
        unit = entityManager.persist(unit);
        entityManager.flush();

        LogPoint logPoint = new LogPoint();
        logPoint.setId(1l);
        logPoint.setName("Точка 1");
        logPoint.setUnit(unit);
        logPoint = entityManager.persist(logPoint);
        entityManager.flush();

        LogPoint found = logPointRepo.findOne(logPoint.getId());
        assertThat(found.getName()).isEqualTo(logPoint.getName());
    }
}
