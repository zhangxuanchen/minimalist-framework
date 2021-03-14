package cn.com.minimalist.framework.simple.eventbus;

public interface EventBusBoardConsumerBridge<T, OUT> {

    EventBusStorage<T> getEventBusStorage();

    EventBusHandlerBundle<T, OUT> getEventBusHandlerBundle();

}
