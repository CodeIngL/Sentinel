package com.alibaba.csp.sentinel.property;

/**
 * @author jialiang.linjl
 */
public interface PropertyListener<T> {

    /**
     * 属性更新时触发
     * @param value
     */
    void configUpdate(T value);

    /**
     * 监听器有归属者时触发
     * @param value
     */
    void configLoad(T value);
}
