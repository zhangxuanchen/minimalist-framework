package cn.com.minimalist.framework.simple.dutylist;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class MethodUtils {

    public  static Map<Integer, Annotation> getAnnotationFromMethodToArray(Method method) {
        Map<Integer, Annotation> parameterMap = new HashMap<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return parameterMap;
        }
        for (int i = 0 ; i < parameterAnnotations.length ; i++) {
            Annotation[] annotationArray = parameterAnnotations[i];
            Annotation annotation = annotationArray[0];
            parameterMap.put(i, annotation);
        }
        return parameterMap;
    }

    public static Set<String> getMethodParameterNameSet(Method method) {
        Set<String> parameterNames = new HashSet<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return parameterNames;
        }
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof ExecuteParam) {
                    ExecuteParam param = (ExecuteParam) annotation;
                    parameterNames.add(param.value());
                }
            }
        }
        return parameterNames;
    }

    public static Set<String> getMethodIgnoreParameterNameSet(Method method) {
        Set<String> parameterNames = new HashSet<>();
        ExecuteIgnoreParam param = method.getAnnotation(ExecuteIgnoreParam.class);
        if(param != null){
            parameterNames.addAll(Arrays.asList(param.value()));
        }
        return parameterNames;
    }
}
