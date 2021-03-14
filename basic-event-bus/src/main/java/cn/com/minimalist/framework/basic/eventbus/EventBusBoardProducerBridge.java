package cn.com.minimalist.framework.basic.eventbus;

 interface EventBusBoardProducerBridge<IN, T> {

    EventBusStorage<T> getEventBusStorage();

    EventBusCreatorBundle<IN, T> getEventBusCreatorBundle();

}
