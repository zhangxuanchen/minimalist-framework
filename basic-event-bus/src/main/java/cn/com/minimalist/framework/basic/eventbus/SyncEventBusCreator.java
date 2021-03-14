package cn.com.minimalist.framework.basic.eventbus;



public interface SyncEventBusCreator<IN, T> {

    T onParser(IN inContent);

}
