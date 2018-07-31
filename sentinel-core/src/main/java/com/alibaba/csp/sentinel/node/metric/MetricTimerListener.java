package com.alibaba.csp.sentinel.node.metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

/**
 * 监测任务
 */
public class MetricTimerListener implements Runnable {

    private static final MetricWriter metricWriter = new MetricWriter(SentinelConfig.singleMetricFileSize(),
        SentinelConfig.totalMetricFileCount());

    @Override
    public void run() {
        Map<Long, List<MetricNode>> maps = new TreeMap<Long, List<MetricNode>>();

        // 每5秒打印一次,把丢弃的seconds都给丢掉。
        for (Entry<ResourceWrapper, ClusterNode> e : ClusterBuilderSlot.getClusterNodeMap().entrySet()) {
            String name = e.getKey().getName();
            ClusterNode node = e.getValue();
            Map<Long, MetricNode> metrics = node.metrics();

            for (Entry<Long, MetricNode> entry : metrics.entrySet()) {
                long time = entry.getKey();
                MetricNode metricNode = entry.getValue();
                metricNode.setName(name);
                if (maps.get(time) == null) {
                    maps.put(time, new ArrayList<MetricNode>());
                }
                List<MetricNode> nodes = maps.get(time);
                nodes.add(entry.getValue());
            }
        }
        if (!maps.isEmpty()) {
            for (Entry<Long, List<MetricNode>> entry : maps.entrySet()) {
                try {
                    metricWriter.write(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    RecordLog.info("write metric error: ", e);
                }
            }
        }

    }

}
