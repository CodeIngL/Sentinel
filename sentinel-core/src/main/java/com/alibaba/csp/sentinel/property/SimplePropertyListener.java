package com.alibaba.csp.sentinel.property;

public abstract class SimplePropertyListener<T> implements PropertyListener<T> {

    @Override
    public void configLoad(T value) {
        configUpdate(value);
    }
}
