package cn.com.minimalist.framework.basic.eventbus;

import java.util.List;

 interface EventBusHandlerBundle<T ,OUT> {

    void registerEventBusHandler(EventBusHandler<T, OUT> eventHandler);

    void unregisterEventBusHandler(EventBusHandler<T, OUT> eventHandler);

    List<EventBusHandler<T, OUT>> getEventBusHandlerList();

}
