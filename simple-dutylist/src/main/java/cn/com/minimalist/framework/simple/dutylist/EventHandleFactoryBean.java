package cn.com.minimalist.framework.simple.dutylist;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class EventHandleFactoryBean<T>  implements FactoryBean<T> {

    private EventHandleProxy eventHandleProxy = new EventHandleProxy();

    private Class<?> handlerInterface;

    private boolean addToConfig = true;

    EventHandleFactoryBean() {

    }

    public EventHandleFactoryBean(Class<T> handlerInterface) {
        this.handlerInterface = handlerInterface;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getObject() throws Exception {
        eventHandleProxy.setHandleInterface(handlerInterface);
        return  (T)Proxy.newProxyInstance(this.handlerInterface.getClassLoader(), new Class[]{this.handlerInterface}, eventHandleProxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getObjectType() {
        return this.handlerInterface;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    //------------- mutators --------------

    /**
     * Sets the mapper interface of the MyBatis mapper
     *
     * @param mapperInterface class of the interface
     */
    public void setHandleInterface(Class<?> mapperInterface) {
        this.handlerInterface = mapperInterface;
    }

    public void setEventHandleBuilderSettings(EventHandleBuilderSettings settings) {
        eventHandleProxy.setEventHandleBuilderSettings(settings);
    }
}