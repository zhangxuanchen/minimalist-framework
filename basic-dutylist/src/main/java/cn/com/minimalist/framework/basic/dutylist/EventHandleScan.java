package cn.com.minimalist.framework.basic.dutylist;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(EventHandleAutoConfiguration.class)
public @interface EventHandleScan {

    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Class<? extends Annotation> annotationClass() default Annotation.class;


    Class<? extends EventHandleFactoryBean> factoryBean() default EventHandleFactoryBean.class;

}
