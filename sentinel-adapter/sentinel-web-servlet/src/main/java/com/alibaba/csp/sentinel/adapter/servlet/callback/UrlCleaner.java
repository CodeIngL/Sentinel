package com.alibaba.csp.sentinel.adapter.servlet.callback;

/***
 * @author youji.zj
 */
public interface UrlCleaner {

    /***
     * <p>Process the url. Some path variables should be handled and unified.</p>
     * <p>e.g. collect_item_relation--10200012121-.html will be converted to collect_item_relation.html</p>
     *
     * @param originUrl original url
     * @return processed url
     */
    String clean(String originUrl);
}
