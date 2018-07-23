package com.alibaba.csp.sentinel.property;

/**
 * @author jialiang.linjl
 */
public interface PropertyListener<T> {

    void configUpdate(T value);

    void configLoad(T value);
}
