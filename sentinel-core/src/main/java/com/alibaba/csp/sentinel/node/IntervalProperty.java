package com.alibaba.csp.sentinel.node;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.property.PropertyListener;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

/***
 * QPS statistics interval.
 *
 * @author youji.zj
 * @author jialiang.linjl
 */
public class IntervalProperty {

    public static volatile int INTERVAL = 1;

    public static void init(SentinelProperty<Integer> dataSource) {
        dataSource.addListener(new FlowIntervalPropertyListener());
    }

    private static class FlowIntervalPropertyListener implements PropertyListener<Integer> {
        @Override
        public void configUpdate(Integer value) {
            if (value == null) {
                value = 1;
            }
            INTERVAL = value;
            RecordLog.info("Init flow interval: " + INTERVAL);
        }

        @Override
        public void configLoad(Integer value) {
            if (value == null) {
                value = 1;
            }
            INTERVAL = value;
            for (ClusterNode node : ClusterBuilderSlot.getClusterNodeMap().values()) {
                node.reset();
            }
            RecordLog.info("Flow interval change received: " + INTERVAL);
        }
    }

}
