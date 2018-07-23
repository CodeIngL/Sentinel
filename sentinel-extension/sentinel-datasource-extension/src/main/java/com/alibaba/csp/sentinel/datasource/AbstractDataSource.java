package com.alibaba.csp.sentinel.datasource;

import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.property.SentinelProperty;

public abstract class AbstractDataSource<S, T> implements DataSource<S, T> {

    protected ConfigParser<S, T> parser;
    protected SentinelProperty<T> property;

    public AbstractDataSource(ConfigParser<S, T> parser) {
        if (parser == null) {
            throw new IllegalArgumentException("parser can't be null");
        }
        this.parser = parser;
        this.property = new DynamicSentinelProperty<T>();
    }

    @Override
    public T loadConfig() throws Exception {
        S readValue = readSource();
        T value = parser.parse(readValue);
        return value;
    }

    public T loadConfig(S conf) throws Exception {
        T value = parser.parse(conf);
        return value;
    }

    @Override
    public SentinelProperty<T> getProperty() {
        return property;
    }

    @Override
    public void writeDataSource(T values) throws Exception {
        throw new UnsupportedOperationException();
    }

}
