package cn.com.minimalist.framework.simple.eventbus;

 interface EventBusBoardConsumerBridge<T, OUT> {

    EventBusStorage<T> getEventBusStorage();

    EventBusHandlerBundle<T, OUT> getEventBusHandlerBundle();

}
