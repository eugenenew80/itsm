package kz.kegoc.bln;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.setMappingFiles(Arrays.asList(
            "dozer/MappingConfig.xml",
            "dozer/MeteringPointDto.xml",
            "dozer/TelemetryDto.xml",
            "dozer/TelemetryExpDto.xml"
        ));

        return mapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ParameterNamesModule());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }


    @Bean
    public Map<String, String> oicPropMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("oic01", env.getProperty("oic.oic01"));
        map.put("oic02", env.getProperty("oic.oic02"));
        map.put("oicDb", env.getProperty("oic.oicDb"));
        map.put("masterDb", env.getProperty("oic.masterDb"));
        map.put("user", env.getProperty("oic.user"));
        map.put("pass", env.getProperty("oic.pass"));
        map.put("port", env.getProperty("oic.port"));
        return map;
    }

    @Autowired
    private Environment env;
}
