package cn.com.minimalist.framework.simple.eventbus;


import cn.com.minimalist.framework.common.ResponseResult;

public interface EventBusHandler<T, OUT> {

     boolean onFilter(T storage);

     OUT onParser(T storage);

     ResponseResult onHandler(OUT outContent);

}
