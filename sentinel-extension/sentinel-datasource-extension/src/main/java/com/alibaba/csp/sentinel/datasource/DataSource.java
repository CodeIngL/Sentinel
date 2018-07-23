package com.alibaba.csp.sentinel.datasource;

import com.alibaba.csp.sentinel.property.SentinelProperty;

/**
 * DataSource is responsible for getting config info.
 *
 * @param <S> source data type
 * @param <T> target data type
 * @author leyou
 */
public interface DataSource<S, T> {

    T loadConfig() throws Exception;

    S readSource() throws Exception;

    SentinelProperty<T> getProperty();

    void writeDataSource(T values) throws Exception;

    void close() throws Exception;
}
