package com.alibaba.csp.sentinel.datasource;

/**
 * Parse config from source data type S to target data type T.
 *
 * @author leyou
 */
public interface ConfigParser<S, T> {
    T parse(S source);
}
