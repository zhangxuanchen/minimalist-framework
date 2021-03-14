package cn.com.minimalist.framework.simple.eventbus;

import java.util.List;

public interface EventBusHandlerBundle<T ,OUT> {

    void registerEventBusHandler(EventBusHandler<T, OUT> eventHandler);

    void unregisterEventBusHandler(EventBusHandler<T, OUT> eventHandler);

    List<EventBusHandler<T, OUT>> getEventBusHandlerList();

}
