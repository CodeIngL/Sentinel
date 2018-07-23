package com.alibaba.csp.sentinel.slots.block.flow;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * @author jialiang.linjl
 */
class ColdFactorProperty {
    public static volatile int coldFactor = 3;

    static {
        String strConfig = SentinelConfig.getConfig(SentinelConfig.COLD_FACTOR);
        if (StringUtil.isBlank(strConfig)) {
            coldFactor = 3;
        } else {
            try {
                coldFactor = Integer.valueOf(strConfig);
            } catch (NumberFormatException e) {
                RecordLog.info(e.getMessage(), e);
            }

            if (coldFactor <= 1) {
                coldFactor = 3;
                RecordLog.info("cold factor should be larger than 3");
            }
        }
    }
}
