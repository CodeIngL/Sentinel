package com.alibaba.csp.sentinel.transport;

/**
 * Heartbeat interface. Sentinel core is responsible for invoking {@link #sendHeartbeat()}
 * at every {@link #intervalMs()} interval.
 * <p>
 * 心跳接口。 Sentinel核心负责在每个{@link #intervalMs()}间隔调用{@link #sendHeartbeat()}。
 * </p>
 *
 * @author leyou
 * @author Eric Zhao
 */
public interface HeartbeatSender {

    /**
     * Send heartbeat to Sentinel Dashboard. Each invocation of this method will send
     * heartbeat once. Sentinel core is responsible for invoking this method
     * at every {@link #intervalMs()} interval.
     *
     * @return whether heartbeat is successfully send.
     * @throws Exception
     */
    boolean sendHeartbeat() throws Exception;

    /**
     * Millisecond interval of every {@link #sendHeartbeat()}
     *
     * @return millisecond interval.
     */
    long intervalMs();
}
