package cn.com.minimalist.framework.basic.eventbus;

import cn.com.minimalist.framework.common.ResponseResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class EventBusOperator<IN, T, OUT> implements
        ApplicationContextAware,
        InitializingBean,
        EventBusConsumer.EventBusConsumerListener,
        EventBusProducer.EventBusProducerListener {

    private static ApplicationContext applicationContext;
    private EventBusConsumer<T ,OUT> eventBusConsumer;
    private EventBusProducer<IN, T> eventBusProducer;
    private EventBusOperatorListener eventBusOperatorListener;

    @Autowired
    private EventBusStorage<T> eventBusStorage;
    @Autowired
    private EventBusCreatorBundle<IN, T> eventBusCreatorBundle;
    @Autowired
    private EventBusHandlerBundle<T, OUT> eventBusHandlerBundle;

    @PostConstruct
    private void init(){
        eventBusProducer = new EventBusProducer<IN, T>() {
            public EventBusStorage<T> getEventBusStorage() {
                return eventBusStorage;
            }
            public EventBusCreatorBundle<IN, T> getEventBusCreatorBundle() {
                return eventBusCreatorBundle;
            }
        };
        eventBusProducer.setEventBusProducerListener(this);
        eventBusConsumer = new EventBusConsumer<T, OUT>() {
            public EventBusStorage<T> getEventBusStorage() {
                return eventBusStorage;
            }
            public EventBusHandlerBundle<T ,OUT> getEventBusHandlerBundle() {
                return eventBusHandlerBundle;
            }
        };
        eventBusConsumer.setEventBusConsumerListener(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] names = applicationContext.getBeanDefinitionNames();

        for(String serviceName : names){
            Object o =  applicationContext.getBean(serviceName);
            if(!(o instanceof EventBusHandler)){
                continue;
            }
            EventBusHandler<T, OUT> eventBusHandler = (EventBusHandler<T, OUT>) o;
            Class res =  o.getClass();
            EventBusHandlerRes handlerRes = (EventBusHandlerRes) res.getAnnotation(EventBusHandlerRes.class);
            if(handlerRes != null && handlerRes.autoLoad()){
                eventBusHandlerBundle.registerEventBusHandler(eventBusHandler);
            }
        }

        for(String serviceName : names){
            Object o =  applicationContext.getBean(serviceName);
            if(!(o instanceof AsyncEventBusCreator)){
                continue;
            }
            AsyncEventBusCreator<IN, T> asyncEventBusCreator = (AsyncEventBusCreator<IN, T>) o;
            Class res =  o.getClass();
            AsyncEventBusCreatorRes creatorRes = (AsyncEventBusCreatorRes) res.getAnnotation(AsyncEventBusCreatorRes.class);
            if(creatorRes != null && creatorRes.autoLoad()){
                eventBusCreatorBundle.registerAsyncEventBusCreator(asyncEventBusCreator);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EventBusOperator.applicationContext = applicationContext;
    }

    @Override
    public void consumerStagesResult(ResponseResult result) {
        if (eventBusOperatorListener != null){
            eventBusOperatorListener.stagesResult(result);
        }
    }

    @Override
    public void consumerError(Exception e) {
        if (eventBusOperatorListener != null){
            eventBusOperatorListener.error(e);
        }
    }

    @Override
    public void producerError(Exception e) {
        if (eventBusOperatorListener != null){
            eventBusOperatorListener.error(e);
        }
    }

    public void setEventBusOperatorListener(EventBusOperatorListener eventBusOperatorListener) {
        this.eventBusOperatorListener = eventBusOperatorListener;
    }

    public interface EventBusOperatorListener{
        void stagesResult(ResponseResult result);
        void error(Exception e);
    }

    public void produce(){
        eventBusProducer.produce();
    }

    public void consume(){
        eventBusConsumer.consume();
    }

    public void registerAsyncEventBusCreator(AsyncEventBusCreator<IN, T> eventCreator){
        eventBusCreatorBundle.registerAsyncEventBusCreator(eventCreator);
    }

    public void unregisterAsyncEventBusCreator(AsyncEventBusCreator<IN, T> eventCreator){
        eventBusCreatorBundle.unregisterAsyncEventBusCreator(eventCreator);
    }

    public void registerEventBusHandler(EventBusHandler<T, OUT> eventHandler){
        eventBusHandlerBundle.registerEventBusHandler(eventHandler);
    }

    public void unregisterEventBusHandler(EventBusHandler<T, OUT> eventHandler){
        eventBusHandlerBundle.unregisterEventBusHandler(eventHandler);
    }

    public ResponseResult<List<ResponseResult>> execute(IN in, SyncEventBusCreator<IN, T> eventCreator){
        T t = eventBusProducer.produce(in, eventCreator);
        return eventBusConsumer.consume(t);
    }
}
