package cn.com.minimalist.framework.simple.eventbus;



public interface SyncEventBusCreator<IN, T> {

    T onParser(IN inContent);

}
