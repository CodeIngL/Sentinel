package com.alibaba.csp.sentinel.adapter.servlet.callback;

/**
 * Registry for URL cleaner and URL block handler.
 *
 * @author youji.zj
 */
public class WebCallbackManager {

    /**
     * URL cleaner
     */
    private static volatile UrlCleaner urlCleaner = new DefaultUrlCleaner();

    /**
     * URL block handler
     */
    private static volatile UrlBlockHandler urlBlockHandler = new DefaultUrlBlockHandler();

    public static UrlCleaner getUrlCleaner() {
        return urlCleaner;
    }

    public static void setUrlCleaner(UrlCleaner urlCleaner) {
        WebCallbackManager.urlCleaner = urlCleaner;
    }

    public static UrlBlockHandler getUrlBlockHandler() {
        return urlBlockHandler;
    }

    public static void setUrlBlockHandler(UrlBlockHandler urlBlockHandler) {
        WebCallbackManager.urlBlockHandler = urlBlockHandler;
    }
}
