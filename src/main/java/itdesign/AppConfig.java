package itdesign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import itdesign.entity.Group;
import itdesign.entity.Status;
import org.dozer.DozerBeanMapper;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.Arrays;

import static java.time.Duration.ofMinutes;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.ExpiryPolicyBuilder.timeToLiveExpiration;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;

@Configuration
@EnableSwagger2
public class AppConfig {

    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.setMappingFiles(Arrays.asList(
            "dozer/MappingConfig.xml",
            "dozer/StatusDto.xml",
            "dozer/GroupDto.xml",
            "dozer/SliceDto.xml",
            "dozer/OrderSliceDto.xml",
            "dozer/ReportCodeDto.xml",
            "dozer/SheetCodeDto.xml",
            "dozer/OrganizationDto.xml"
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
    public CacheManager ehcacheManager() {
        return CacheManagerBuilder.newCacheManagerBuilder()
            .withCache("statusCache",
                newCacheConfigurationBuilder(Long.class, Status.class, heap(20))
                        .withExpiry(timeToLiveExpiration(ofMinutes(60))).build())
            .withCache("groupCache",
                newCacheConfigurationBuilder(Long.class, Group.class, heap(300))
                    .withExpiry(timeToLiveExpiration(ofMinutes(10))).build())
            .build(true);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("itdesign.web"))
            .paths(PathSelectors.any())
            .build();
    }

    @Autowired
    private Environment env;
}
