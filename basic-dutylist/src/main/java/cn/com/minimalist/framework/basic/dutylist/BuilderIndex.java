package cn.com.minimalist.framework.basic.dutylist;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BuilderIndex {
    String[] value() default "";
}
