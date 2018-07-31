package com.alibaba.csp.sentinel.transport.init;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.transport.HeartbeatSender;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;

/**
 * Global init function for heartbeat sender.
 * <p>
 * 心跳发送器的全局初始化函数功能。
 * </p>
 *
 * @author Eric Zhao
 */
public class HeartbeatSenderInitFunc implements InitFunc {

    private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

    @Override
    public void init() throws Exception {
        long heartBeatInterval = -1;
        try {
            heartBeatInterval = TransportConfig.getHeartbeatIntervalMs();
            RecordLog.info("system property heartbeat interval set: " + heartBeatInterval);
        } catch (Exception ex) {
            RecordLog.info("Parse heartbeat interval failed, use that in code, " + ex.getMessage());
        }
        //spi查找HeartbeatSender的实现
        ServiceLoader<HeartbeatSender> loader = ServiceLoader.load(HeartbeatSender.class);
        Iterator<HeartbeatSender> iterator = loader.iterator();
        if (iterator.hasNext()) {
            final HeartbeatSender sender = iterator.next();
            if (iterator.hasNext()) { //仅仅有一个实现
                throw new IllegalStateException("Only single heartbeat sender can be scheduled");
            } else {
                long interval = sender.intervalMs();
                if (heartBeatInterval != -1) {
                    interval = heartBeatInterval;
                }
                pool.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sender.sendHeartbeat();
                        } catch (Exception e) {
                            e.printStackTrace();
                            RecordLog.info("[HeartbeatSender] Send heartbeat error", e);
                        }
                    }
                }, 10000, interval, TimeUnit.MILLISECONDS);
                RecordLog.info("[HeartbeatSenderInit] HeartbeatSender started: "
                        + sender.getClass().getCanonicalName());
            }
        }
    }
}
