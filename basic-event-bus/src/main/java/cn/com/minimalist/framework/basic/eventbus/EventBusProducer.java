package cn.com.minimalist.framework.basic.eventbus;

import java.util.List;

/**
 * 事件处理提供者
 */
abstract class EventBusProducer<IN, T> implements EventBusBoardProducerBridge<IN, T> {

    void produce() {
        EventBusCreatorBundle<IN, T> eventBusCreatorBundle = getEventBusCreatorBundle();
        List<AsyncEventBusCreator<IN, T>> eventCreatorList = eventBusCreatorBundle.getAsyncEventBusCreatorList();
        for (AsyncEventBusCreator<IN, T> eventCreator : eventCreatorList) {
            try {
                eventCreator.onPre();
                while (eventCreator.onHasNext()) {
                    IN o = eventCreator.onNext();
                    T busEvent = produce(o, eventCreator);
                    if (busEvent == null) {
                        eventCreator.onAck(o);
                        continue;
                    }
                    EventBusStorage<T> eventBusStorage = getEventBusStorage();
                    boolean saveSuccess = eventBusStorage.storageBusEvent(busEvent);
                    if (saveSuccess) {
                        eventCreator.onAck(o);
                    }
                }
                eventCreator.onClose();
            } catch (Exception e) {
                if(eventBusProducerListener != null){
                    eventBusProducerListener.producerError(e);
                }
            }
        }
    }

    T produce(IN in, SyncEventBusCreator<IN, T> eventCreator) {
        return eventCreator.onParser(in);
    }

    volatile EventBusProducerListener eventBusProducerListener;

    void setEventBusProducerListener(EventBusProducerListener eventBusProducerListener) {
        this.eventBusProducerListener = eventBusProducerListener;
    }

    interface EventBusProducerListener {
        void producerError(Exception e);
    }
}
