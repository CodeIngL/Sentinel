package com.alibaba.csp.sentinel.transport.heartbeat;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.alibaba.csp.sentinel.util.TimeUtil;

/**
 * Heart beat message entity.
 * The message consists of key-value pair parameters.
 *
 * @author leyou
 */
public class HeartbeatMessage {

    private final Map<String, String> message = new HashMap<String, String>();

    public HeartbeatMessage() {
        message.put("hostname", HostNameUtil.getHostName());
        message.put("ip", HostNameUtil.getIp());
        message.put("app", AppNameUtil.getAppName());
        message.put("port", String.valueOf(TransportConfig.getPort()));
    }

    public HeartbeatMessage registerInformation(String key, String value) {
        message.put(key, value);
        return this;
    }

    public Map<String, String> generateCurrentMessage() {
        message.put("version", String.valueOf(TimeUtil.currentTimeMillis()));
        message.put("port", String.valueOf(TransportConfig.getPort()));
        return message;
    }
}
