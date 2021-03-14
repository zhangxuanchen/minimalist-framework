package cn.com.minimalist.framework.simple.eventbus;


public interface AsyncEventBusCreator<IN, T> extends SyncEventBusCreator<IN, T>{

    void onPre();

    IN onNext();

    boolean onHasNext();

    void onClose();

    void onAck(IN in);

}
