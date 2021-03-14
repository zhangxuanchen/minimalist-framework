package cn.com.minimalist.framework.simple.dutylist;

import cn.com.minimalist.framework.common.ResponseResult;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventHandleBuilderSettings {

    private static ApplicationContext applicationContext;

    @Autowired
    private ExecutorContainer executorContainer;

    private Set<String> eventInitParameterSet = new HashSet<>();

    //事件处理
    private final Map<String, List<Object>> eventHandlerMapper = new ConcurrentHashMap<>();
    private final Map<String, List<Class>> eventClassMapper = new ConcurrentHashMap<>();

    public void addEventInitParameter(String... parameters){
        Collections.addAll(eventInitParameterSet, parameters);
    }

    List<Object> getEventHandler(TransferEntity transferEntity) {
        String index = transferEntity.getIndex();
        String event = transferEntity.getEvent();
        String key = index + "_" + event;
        return eventHandlerMapper.get(key);
    }

    /**
     * register event builder
     *
     * @return
     */
    <T extends BuilderMapper> void registerBuilder(T builder) {
        Method[] methods = builder.getClass().getMethods();
        for (Method builderMethod : methods) {
            BuilderIndex methodIndex = builderMethod.getAnnotation(BuilderIndex.class);
            if (methodIndex == null) {
                continue;
            }
            String[] values = methodIndex.value();
            for(String value : values){
                String key =  value + "_" + builderMethod.getName();
                List<Class> nodeList = null;
                try {
                    nodeList = (List<Class>) builderMethod.invoke(builder);
                    eventClassMapper.put(key, nodeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * register event handler
     */
    <T extends HandlerMapper> void registerHandler(Class<T> handlerClass) {
        Method[] handlerMethods = handlerClass.getMethods();
        for (Method handlerMethod : handlerMethods) {
            Class<ResponseResult> returnClass = (Class<ResponseResult>) handlerMethod.getReturnType();
            if(!returnClass.equals(ResponseResult.class)){
                continue;
            }
            String event = handlerMethod.getName();
            Set<String> indexSet = eventClassMapper.keySet();
            int exitsMethod = 0;
            for (String index : indexSet) {
                if (index.endsWith(event)) {
                    exitsMethod++;
                }
            }
            if(exitsMethod == 0){
                throw new RuntimeException("该方法不存在");
            }
            for (String index : indexSet) {
                if (!index.endsWith(event)) {
                    continue;
                }
                List<Class> nodeClassList = eventClassMapper.get(index);
                //方法中存在的参数
                Set<String> inputParameterSet = MethodUtils.getMethodParameterNameSet(handlerMethod);
                inputParameterSet.addAll(eventInitParameterSet);
                //获取事件中的参数
                List<Object> beanList = new ArrayList<>();
                for (Class nodeClass : nodeClassList) {
                    //查找节点中方法run()的方法
                    Method[] methods = nodeClass.getMethods();
                    for (Method m : methods) {
                        String methodName = m.getName();
                        if ("run".equals(methodName)) {
                            Set<String> nodeMethodParameterSet = MethodUtils.getMethodParameterNameSet(m);
                            Set<String> methodIgnoreParameterNameSet = MethodUtils.getMethodIgnoreParameterNameSet(m);
                            nodeMethodParameterSet.removeAll(methodIgnoreParameterNameSet);
                            for (String next : nodeMethodParameterSet) {
                                if (!inputParameterSet.contains(next)) {
                                    throw new RuntimeException("参数检查不正确");
                                }
                            }
                            ExecuteAddParam annotation = m.getAnnotation(ExecuteAddParam.class);
                            if (annotation != null && annotation.value().length > 0) {
                                inputParameterSet.addAll(Arrays.asList(annotation.value()));
                            }
                        }
                    }
                    Object bean = applicationContext.getBean(lowerFirstCase(nodeClass.getSimpleName()));
                    if(bean == null){
                        throw new RuntimeException("node 节点声明错误");
                    }
                    beanList.add(bean);
                }
                eventHandlerMapper.put(index, beanList);
            }
        }
    }

    public static String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        //首字母小写方法，大写会变成小写，如果小写首字母会消失
        chars[0] +=32;
        return String.valueOf(chars);
    }

    ExecutorContainer getExecutorContainer() {
        return executorContainer;
    }

    public void setExecutorContainer(ExecutorContainer executorContainer) {
        this.executorContainer = executorContainer;
    }

    public interface OnLoadTransferEntity {
        TransferEntity onLoadTransferEntity(TransferEntity transferEntity, EventHandleBuilderSettings settings);
    }

    private volatile OnLoadTransferEntity onLoadTransferEntity;

    OnLoadTransferEntity getOnLoadTransferEntity() {
        return onLoadTransferEntity;
    }

    public void setOnLoadTransferEntity(OnLoadTransferEntity onLoadTransferEntity) {
        this.onLoadTransferEntity = onLoadTransferEntity;
    }

    static void setApplicationContext(ApplicationContext applicationContext) {
        EventHandleBuilderSettings.applicationContext = applicationContext;
    }
}
