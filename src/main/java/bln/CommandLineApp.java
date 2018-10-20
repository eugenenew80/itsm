package bln;

import bln.entity.ArcType;
import bln.entity.LogPoint;
import bln.repo.ArcTypeRepo;
import bln.repo.LogPointRepo;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component@Order(1)
@AllArgsConstructor
public class CommandLineApp implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private final EntityManager em;
    private final LogPointRepo logPointRepo;
    private final ArcTypeRepo arcTypeRepo;
    private final ApplicationArguments arguments;

    @Override
    public void run(String... strings)  {
        run();
    }

    public void run() {
        if (arguments.containsOption("update-db"))
            updateDb();

        if (arguments.containsOption("add-arc")) {
            List<String> values = arguments.getOptionValues("arc.id");
            String arcId = values!=null && values.size() > 0 ? values.get(0) : null;

            values = arguments.getOptionValues("arc.start-date");
            String arcStartTime = values!=null && values.size() > 0 ? values.get(0) : null;

            Long oicArcId = Optional.ofNullable(Long.parseLong(arcId)).orElse(null);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime startTime = arcStartTime != null ? LocalDate.parse(arcStartTime, formatter).atStartOfDay().minusMinutes(60) : null;

            if (oicArcId == null) {
                logger.error("Parameter arc.oicArcId required");
                return;
            }

            ArcType arcType = arcTypeRepo.findOne("MIN-60");
            if (arcType == null) {
                arcType = new ArcType();
                arcType.setCode("MIN-60");
                arcType.setStep(60l);
                arcType.setName("Часовый архив телеизмерений");
            }
            arcType.setIsActive(true);
            arcType.setOicArcId(oicArcId);
            arcType.setLastLoadTime(Optional.ofNullable(startTime).orElse(arcType.getLastLoadTime()));
            arcTypeRepo.save(arcType);
        }

        if (arguments.containsOption("add-point")) {
            List<String> values = arguments.getOptionValues("point.ti");
            String tiNum = values!=null && values.size() > 0 ? values.get(0) : null;

            values = arguments.getOptionValues("point.name");
            String tiName = values!=null && values.size() > 0 ? values.get(0) : null;

            values = arguments.getOptionValues("point.start-date");
            String tiStartTime = values!=null && values.size() > 0 ? values.get(0) : null;

            Long id = Optional.ofNullable(Long.parseLong(tiNum)).orElse(null);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime startTime = tiStartTime != null ? LocalDate.parse(tiStartTime, formatter).atStartOfDay() : null;

            if (id == null) {
                logger.error("Parameter point.ti required");
                return;
            }

            LogPoint point = logPointRepo.findOne(id);
            if (point == null) {
                point = new LogPoint();
                point.setId(id);
                point.setIsNewPoint(true);
            }
            point.setName(Optional.ofNullable(tiName).orElse(point.getName()) );
            point.setStartTime(Optional.ofNullable(startTime).orElse(point.getStartTime()));
            logPointRepo.save(point);
        }
    }

    private void updateDb() {
        try {
            Connection conn = getDataSource().getConnection();
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataSource getDataSource() {
        EntityManagerFactoryInfo info = (EntityManagerFactoryInfo) em.getEntityManagerFactory();
        return info.getDataSource();
    }
}
