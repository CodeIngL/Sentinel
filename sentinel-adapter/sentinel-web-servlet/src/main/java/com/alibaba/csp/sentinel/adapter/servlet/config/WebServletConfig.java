package com.alibaba.csp.sentinel.adapter.servlet.config;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import com.alibaba.csp.sentinel.adapter.servlet.CommonTotalFilter;
import com.alibaba.csp.sentinel.config.SentinelConfig;

/**
 * @author leyou
 */
public class WebServletConfig {

    public static final String BLOCK_PAGE = "csp.sentinel.web.servlet.block.page";

    /**
     * Get redirecting page when Sentinel blocking for {@link CommonFilter} or
     * {@link CommonTotalFilter} occurs.
     *
     * @return the block page URL, maybe null if not configured.
     */
    public static String getBlockPage() {
        return SentinelConfig.getConfig(BLOCK_PAGE);
    }

    public static void setBlockPage(String blockPage) {
        SentinelConfig.setConfig(BLOCK_PAGE, blockPage);
    }
}
