package cn.com.minimalist.framework.basic.dutylist;

import cn.com.minimalist.framework.common.ResponseResult;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@Service
 class Executor {

    private static final String BEFORE = "before";
    private static final String AFTER = "after";
    private static final String RUN = "run";

     ExecuteResult run(TransferEntity buildEntity, List<Object> buildList) throws InvocationTargetException, IllegalAccessException {
        if (buildList.size() == 0) {
            return ExecuteResult.pass();
        }
        for (Object builder : buildList) {
            ExecuteResult beforeResult = runStep(BEFORE, buildEntity, builder);
            if (!beforeResult.isPass()) {
                return beforeResult;
            }
            ExecuteResult runResult = runStep(RUN, buildEntity, builder);
            if (!runResult.isPass()) {
                return runResult;
            }
            ExecuteResult afterResult = runStep(AFTER, buildEntity, builder);
            if (!afterResult.isPass()) {
                return afterResult;
            }
        }
        return ExecuteResult.pass();
    }

    public ExecuteResult runStep(String step, TransferEntity buildEntity, Object builder) throws InvocationTargetException, IllegalAccessException {
        //查找run方法
        Method[] methods = getTarget(builder).getClass().getMethods();
        for (Method method : methods) {
            if (step.equals(method.getName())) {
                List<Object> argsList = new ArrayList<>();
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                for (Annotation[] annotations : parameterAnnotations) {
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType() == ExecuteParam.class) {
                            ExecuteParam param = ((ExecuteParam) annotation);
                            Object o = buildEntity.get(param.value());
                            argsList.add(o);
                        }
                    }
                }

                ExecuteResult taskExecutorResult = (ExecuteResult) method.invoke(builder, argsList.toArray());
                if (taskExecutorResult.isPass()) {
                    TransferParam[] resultParams = taskExecutorResult.getResultParams();
                    if (resultParams != null && resultParams.length > 0) {
                        for (TransferParam param : resultParams) {
                            String key = param.getKey();
                            Object value = param.getValue();
                            buildEntity.put(key, value);
                        }
                    }
                    Object result = taskExecutorResult.getResult();
                    if (result != null) {
                        buildEntity.setReturnResult(ResponseResult.success(result));
                    }
                    return taskExecutorResult;
                }

                if (taskExecutorResult.isSuccess()) {
                    Object result = taskExecutorResult.getResult();
                    if (result != null) {
                        buildEntity.setReturnResult(ResponseResult.success(result));
                    }
                }
                return taskExecutorResult;
            }
        }
        return ExecuteResult.pass();
    }

    public Object getTarget(Object beanInstance) {
        if (!AopUtils.isAopProxy(beanInstance)) {
            return beanInstance;
        } else if (AopUtils.isCglibProxy(beanInstance)) {
            try {
                Field h = beanInstance.getClass().getDeclaredField("CGLIB$CALLBACK_0");
                h.setAccessible(true);
                Object dynamicAdvisedInterceptor = h.get(beanInstance);
                Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
                advised.setAccessible(true);
                Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
                return target;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //不要删除public关键字 ,否则事务将不生效
    @Transactional(rollbackFor = Exception.class)
    public ExecuteResult runT(TransferEntity buildEntity, List<Object> dutyList) throws Exception {
        ExecuteResult runResult = run(buildEntity, dutyList);
        if (!StringUtils.isEmpty(runResult.getErrorMsg())) {
            //手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return runResult;
    }

}
