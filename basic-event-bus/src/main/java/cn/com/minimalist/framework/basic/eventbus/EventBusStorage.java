package cn.com.minimalist.framework.basic.eventbus;

import java.util.List;

public interface EventBusStorage<T> {

    boolean storageBusEvent(T t);

    List<T> getUnprocessedBusEvent(int pageNum, int pageSize);

    boolean successBusEvent(T t);

    boolean failBusEvent(T t);
}
