package com.alibaba.csp.sentinel.datasource;

import com.alibaba.csp.sentinel.property.NoOpSentinelProperty;
import com.alibaba.csp.sentinel.property.SentinelProperty;

/**
 * A {@link DataSource} based on nothing. {@link EmptyDataSource#getProperty()} will always return the same cached
 * {@link SentinelProperty} that doing nothing.
 * <br/>
 * This class is used when we want to use default settings instead of configs from the {@link DataSource}
 *
 * @author leyou
 */
public class EmptyDataSource implements DataSource<Object, Object> {

    public static final DataSource<Object, Object> EMPTY_DATASOURCE = new EmptyDataSource();

    private static final SentinelProperty<Object> property = new NoOpSentinelProperty();

    private EmptyDataSource() { }

    @Override
    public Object loadConfig() throws Exception {
        return null;
    }

    @Override
    public Object readSource() throws Exception {
        return null;
    }

    @Override
    public SentinelProperty<Object> getProperty() {
        return property;
    }

    @Override
    public void close() throws Exception { }

    @Override
    public void writeDataSource(Object config) throws Exception {
        throw new UnsupportedOperationException();
    }

}
