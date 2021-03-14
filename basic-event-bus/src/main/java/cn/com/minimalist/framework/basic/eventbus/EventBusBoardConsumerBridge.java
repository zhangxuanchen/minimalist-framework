package cn.com.minimalist.framework.basic.eventbus;

 interface EventBusBoardConsumerBridge<T, OUT> {

    EventBusStorage<T> getEventBusStorage();

    EventBusHandlerBundle<T, OUT> getEventBusHandlerBundle();

}
