package cn.com.minimalist.framework.basic.dutylist;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EventHandleAutoConfiguration implements ImportBeanDefinitionRegistrar, ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(EventHandleAutoConfiguration.class);

    @Autowired
    private EventHandleBuilderSettings settings;

    private static EventHandleScanner scanner;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //load event handler
        registerEventHandler(importingClassMetadata, registry);
    }

    /**
     * register event handler
     * @param importingClassMetadata
     * @param registry
     */
    private void registerEventHandler(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EventHandleScan.class.getName()));
        scanner = new EventHandleScanner(registry);
        scanner.setAnnotationClass(HandlerMapperRes.class);
        scanner.setMarkerInterface(HandlerMapper.class);
        scanner.registerFilters();
        List<String> basePackages = new ArrayList<String>();
        for (String pkg : annoAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        System.out.println("需要加载的路径:" + JSON.toJSONString(basePackages));
        logger.debug("需要加载的路径:" + JSON.toJSONString(basePackages));
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }

    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EventHandleAutoConfiguration.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventHandleBuilderSettings.setApplicationContext(applicationContext);
        String[] names = applicationContext.getBeanDefinitionNames();
        System.out.println("names:" + names.length);

        //装载builder节点
        for(String serviceName : names){
            Object o =  applicationContext.getBean(serviceName);
            Class res =  o.getClass();
            BuilderMapperRes builderRes = (BuilderMapperRes) res.getAnnotation(BuilderMapperRes.class);

            if(builderRes == null){
                continue;
            }

            System.out.println("111serviceName:" + res);
            System.out.println("builderRes:" + builderRes);

            settings.registerBuilder((BuilderMapper) o);
        }

        //装载handler节点
        for(String serviceName : names){
            Object o =  applicationContext.getBean(serviceName);
            if(!(o instanceof HandlerMapper)){
                continue;
            }
            Class res = o.getClass();

            Class[] interfaces = res.getInterfaces();
            for(Class  c : interfaces){
                 try{
                     c.asSubclass(HandlerMapper.class);
                 }catch (Exception e){
                     continue;
                 }
                 System.out.println("通过");
                 settings.registerHandler(c);
            }
        }
        scanner.setEventHandleBuilderSettings(settings);
    }
}
