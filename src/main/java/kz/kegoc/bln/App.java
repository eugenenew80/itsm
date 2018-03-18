package kz.kegoc.bln;

import kz.kegoc.bln.test.ch2.MessageRenderer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(
    basePackageClasses = { App.class, Jsr310JpaConverters.class }
)
@SpringBootApplication
@EnableScheduling
//@ImportResource("classpath:META-INF/app-config.xml")
public class App {
    public static void main(String[] args) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext( "META-INF/app-config.xml");
        MessageRenderer mr = ctx. getBean ( "renderer", MessageRenderer. class);
        mr.render();

        SpringApplication.run(App.class, args);
    }
}
