package kz.kegoc.bln;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@EnableAutoConfiguration(exclude = LiquibaseAutoConfiguration.class)
@EntityScan(
    basePackageClasses = { App.class, Jsr310JpaConverters.class }
)
@SpringBootApplication
@EnableScheduling
public class App  {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        if (args.length > 0 && args[0] !=null && args[0].equals("init")) {
            update();
            return;
        }
        SpringApplication.run(App.class, args);
    }

    private static void update()  {
        Connection conn = null;
        try {
            String url = "jdbc:postgresql://localhost/postgres?user=postgres&password=password";

            conn = DriverManager.getConnection(url);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.close();
                }
                catch (SQLException e) { }
            }
        }
    }
}
