package cn.com.minimalist.framework.basic.dutylist;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ExecuteIndex {
    String value() default "";
}
