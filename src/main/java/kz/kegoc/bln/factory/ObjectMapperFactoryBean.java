package kz.kegoc.bln.factory;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import java.util.Set;

public class ObjectMapperFactoryBean implements FactoryBean<ObjectMapper>, InitializingBean {

    @Override
    public ObjectMapper getObject() throws Exception { return this.mapper; }

    @Override
    public Class<?> getObjectType() { return ObjectMapper.class; }

    @Override
    public boolean isSingleton() { return true; }

    @Override
    public void afterPropertiesSet() throws Exception {
        modules.forEach(mapper::registerModule);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

    private Set<Module> modules;
    private ObjectMapper mapper = new ObjectMapper();;
}
