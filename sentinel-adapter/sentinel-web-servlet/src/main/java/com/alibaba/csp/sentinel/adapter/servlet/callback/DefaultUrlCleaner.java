package com.alibaba.csp.sentinel.adapter.servlet.callback;

/***
 * @author youji.zj
 */
public class DefaultUrlCleaner implements UrlCleaner {

    @Override
    public String clean(String originUrl) {
        return originUrl;
    }
}
