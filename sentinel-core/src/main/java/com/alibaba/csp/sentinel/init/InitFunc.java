package com.alibaba.csp.sentinel.init;

/**
 * spi接口进行提供初始化扩展点
 *
 * @author Eric Zhao
 */
public interface InitFunc {

    void init() throws Exception;
}
