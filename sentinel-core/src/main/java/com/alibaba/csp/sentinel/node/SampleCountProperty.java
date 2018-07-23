package com.alibaba.csp.sentinel.node;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.property.SimplePropertyListener;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

/**
 * @author jialiang.linjl
 */
public class SampleCountProperty {

    public static volatile int sampleCount = 2;

    public static void init(SentinelProperty<Integer> property) {

        try {
            property.addListener(new SimplePropertyListener<Integer>() {
                @Override
                public void configUpdate(Integer value) {
                    if (value != null) {
                        sampleCount = value;
                        // Reset the value.
                        for (ClusterNode node : ClusterBuilderSlot.getClusterNodeMap().values()) {
                            node.reset();
                        }
                    }
                    RecordLog.info("Current SampleCount: " + sampleCount);
                }

            });
        } catch (Exception e) {
            RecordLog.info(e.getMessage(), e);
        }
    }
}
