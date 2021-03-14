package cn.com.minimalist.framework.simple.eventbus;

 interface EventBusBoardProducerBridge<IN, T> {

    EventBusStorage<T> getEventBusStorage();

    EventBusCreatorBundle<IN, T> getEventBusCreatorBundle();

}
