package cn.com.minimalist.framework.basic.eventbus;

import java.util.List;

 interface EventBusCreatorBundle<IN, T> {

    void registerAsyncEventBusCreator(AsyncEventBusCreator<IN, T> eventCreator);

    void unregisterAsyncEventBusCreator(AsyncEventBusCreator<IN, T> eventCreator);

    List<AsyncEventBusCreator<IN, T>> getAsyncEventBusCreatorList();

}
