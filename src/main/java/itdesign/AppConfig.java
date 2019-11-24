package itdesign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import itdesign.entity.Group;
import itdesign.entity.Status;
import itdesign.web.security.UserInfo;
import org.dozer.DozerBeanMapper;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofMinutes;
import static java.util.Arrays.*;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.ExpiryPolicyBuilder.timeToLiveExpiration;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;

@Configuration
@EnableSwagger2
public class AppConfig {

    @Value( "${itdesign.redis.address}" )
    private final String redisAddress = "18.140.232.52:6379";

    @Value( "${itdesign.redis.password}" )
    private final String redisPassword = "123456";

    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        DozerBeanMapper mapper = new DozerBeanMapper();
         mapper.setMappingFiles(asList(
            "dozer/MappingConfig.xml",
            "dozer/StatusDto.xml",
            "dozer/GroupDto.xml",
            "dozer/SliceDto.xml",
            "dozer/OrderSliceDto.xml",
            "dozer/ReportCodeDto.xml",
            "dozer/ReportCodeDto2.xml",
            "dozer/SheetCodeDto.xml",
            "dozer/OrganizationDto.xml",
            "dozer/TemplateCodeDto.xml"
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
                newCacheConfigurationBuilder(String.class, Status.class, heap(20))
                        .withExpiry(timeToLiveExpiration(ofMinutes(60))).build())
            .withCache("groupCache",
                newCacheConfigurationBuilder(String.class, Group.class, heap(300))
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

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress(redisAddress)
            .setPassword(redisPassword);

        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    @Bean
    public Map<String, Set<String>> mapMethodsOnRoles() {
        ConcurrentHashMap<String, Set<String>> map = new ConcurrentHashMap<>();
        map.put("itdesign.web.SliceRestController.create",      new HashSet<>(asList("SLICE_ORDER")));
        map.put("itdesign.web.SliceRestController.send",        new HashSet<>(asList("SLICE_SEND_ON_APPROVE")));
        map.put("itdesign.web.SliceRestController.approve",     new HashSet<>(asList("SLICE_APPROVE")));
        map.put("itdesign.web.SliceRestController.delete",      new HashSet<>(asList("SLICE_DELETE")));
        map.put("itdesign.web.SliceRestController.confirm",     new HashSet<>(asList("SLICE_CONFIRM")));
        map.put("itdesign.web.SliceRestController.preliminary", new HashSet<>(asList("SLICE_SET_ON_PRELIMINARY")));
        map.put("itdesign.web.SliceRestController.cancel",      new HashSet<>(asList("SLICE_CANCEL")));
        return map;
    }
}
