package cn.com.minimalist.framework.basic.eventbus;

import cn.com.minimalist.framework.common.ResponseResult;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件处理消费者
 */
abstract class EventBusConsumer<T, OUT> implements EventBusBoardConsumerBridge<T, OUT> {

    void consume() {
        int pageNum = 1;
        int pageSize = 100;
        List<T> unprocessedBusEvent = null;
        while (true) {
            EventBusStorage<T> eventBusStorage = getEventBusStorage();
            unprocessedBusEvent = eventBusStorage.getUnprocessedBusEvent(pageNum, pageSize);
            if (unprocessedBusEvent == null || unprocessedBusEvent.size() == 0) {
                break;
            }
            for (T t : unprocessedBusEvent) {
                ResponseResult<List<ResponseResult>> consumeResult = consume(t);
                if (consumeResult != null) {
                    updateBusEventStatus(t, consumeResult.isSuccess());
                }
            }
            pageNum++;
        }
    }

    ResponseResult<List<ResponseResult>> consume(T t) {
        EventBusHandlerBundle<T, OUT> eventBusHandlerBundle = getEventBusHandlerBundle();
        List<EventBusHandler<T, OUT>> eventBusHandlerList = eventBusHandlerBundle.getEventBusHandlerList();
        boolean success = true;
        List<ResponseResult> responseResultList = new ArrayList<>();
        for (EventBusHandler<T, OUT> eventHandler : eventBusHandlerList) {
            try {
                boolean filter = eventHandler.onFilter(t);
                if (!filter) {
                    continue;
                }
                OUT o = eventHandler.onParser(t);
                ResponseResult responseResult = eventHandler.onHandler(o);
                responseResultList.add(responseResult);
                boolean result = responseResult.isSuccess();
                success = success&result;
                if(eventBusConsumerListener != null){
                    eventBusConsumerListener.consumerStagesResult(responseResult);
                }
            } catch (Exception e) {
                success = false;
                if(eventBusConsumerListener != null){
                    eventBusConsumerListener.consumerError(e);
                }
            }
        }
        //处理完成返回框架最终处理结果
        return ResponseResult.success(responseResultList);
    }


    private void updateBusEventStatus(T t, boolean success) {
        try {
            EventBusStorage<T> eventBusStorage = getEventBusStorage();
            if (success) {
                eventBusStorage.successBusEvent(t);
            } else {
                eventBusStorage.failBusEvent(t);
            }
        }catch (Exception e){
            if(eventBusConsumerListener != null){
                eventBusConsumerListener.consumerError(e);
            }
        }
    }

    volatile EventBusConsumerListener eventBusConsumerListener;

    void setEventBusConsumerListener(EventBusConsumerListener eventBusConsumerListener) {
        this.eventBusConsumerListener = eventBusConsumerListener;
    }

    interface EventBusConsumerListener{
        void consumerStagesResult(ResponseResult result);
        void consumerError(Exception e);
    }

}
