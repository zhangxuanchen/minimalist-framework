package cn.com.minimalist.framework.simple.dutylist;

import cn.com.minimalist.framework.common.ResponseResult;
import com.alibaba.fastjson.JSONObject;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventHandleProxy implements InvocationHandler {

    private static EventHandleBuilderSettings eventHandleBuilderSettings;

    private Class<?> handleInterface;

    public void setHandleInterface(Class<?> handleInterface) {
        this.handleInterface = handleInterface;
    }

    public void setEventHandleBuilderSettings(EventHandleBuilderSettings eventHandleBuilderSettings) {
        EventHandleProxy.eventHandleBuilderSettings = eventHandleBuilderSettings;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String event = method.getName();
        Map<Integer, Annotation> annotationFromMethodToArray = MethodUtils.getAnnotationFromMethodToArray(method);
        JSONObject parameterJSON = new JSONObject();
        Set<Integer> annotationTagSet = annotationFromMethodToArray.keySet();
        String index = "";
        for(Integer tag : annotationTagSet){
            Object value = args[tag];
            Annotation annotation = annotationFromMethodToArray.get(tag);
            if(annotation instanceof ExecuteParam){
                ExecuteParam executeParam = (ExecuteParam) annotation;
                parameterJSON.put(executeParam.value(), value);
            }
            if(annotation instanceof ExecuteIndex){
                index = (String) value;
            }
        }
        //获取参数
        return handleEvent(index, event, parameterJSON);
    }

    private ResponseResult handleEvent(String index, String event, JSONObject parameter) throws Throwable {
        EventHandleBuilderSettings.OnLoadTransferEntity onLoadTransferEntity = eventHandleBuilderSettings.getOnLoadTransferEntity();
        TransferEntity transferEntity = new TransferEntity();
        transferEntity.setEvent(event);
        transferEntity.setIndex(index);
        if(onLoadTransferEntity != null){
            transferEntity = onLoadTransferEntity.onLoadTransferEntity(transferEntity, eventHandleBuilderSettings);
        }
        if(parameter != null){
            transferEntity.setMap(parameter.getInnerMap());
        }
        try {
            List<Object> handleList = eventHandleBuilderSettings.getEventHandler(transferEntity);
           ExecutorContainer executorContainer = eventHandleBuilderSettings.getExecutorContainer();
            return executorContainer.executeTask(transferEntity, handleList);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

}
