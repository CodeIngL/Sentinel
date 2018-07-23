package com.alibaba.csp.sentinel.transport.heartbeat;

import java.util.Arrays;

import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.alibaba.csp.sentinel.transport.HeartbeatSender;
import com.alibaba.csp.sentinel.util.PidUtil;
import com.alibaba.csp.sentinel.util.StringUtil;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author Eric Zhao
 * @author leyou
 */
public class HttpHeartbeatSender implements HeartbeatSender {

    private final CloseableHttpClient client;

    private final int timeoutMs = 3000;
    private final RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(timeoutMs)
        .setConnectTimeout(timeoutMs)
        .setSocketTimeout(timeoutMs)
        .build();

    private String consoleHost;
    private int consolePort;

    public HttpHeartbeatSender() {
        this.client = HttpClients.createDefault();
        String consoleServer = TransportConfig.getConsoleServer();
        if (StringUtil.isEmpty(consoleServer)) {
            RecordLog.info("[Heartbeat] Console server address is not configured!");
        } else {
            String consoleHost = consoleServer;
            int consolePort = 80;
            if (consoleServer.contains(",")) {
                consoleHost = consoleServer.split(",")[0];
            }
            if (consoleHost.contains(":")) {
                String[] strs = consoleServer.split(":");
                consoleHost = strs[0];
                consolePort = Integer.parseInt(strs[1]);
            }
            this.consoleHost = consoleHost;
            this.consolePort = consolePort;
        }

    }

    @Override
    public boolean sendHeartbeat() throws Exception {
        if (StringUtil.isEmpty(consoleHost)) {
            return false;
        }
        RecordLog.info(String.format("[Heartbeat] Sending heartbeat to %s:%d", consoleHost, consolePort));

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("http").setHost(consoleHost).setPort(consolePort)
            .setPath("/registry/machine")
            .setParameter("app", AppNameUtil.getAppName())
            .setParameter("version", String.valueOf(System.currentTimeMillis()))
            .setParameter("hostname", HostNameUtil.getHostName())
            .setParameter("ip", HostNameUtil.getIp())
            .setParameter("port", TransportConfig.getPort())
            .setParameter("pid", String.valueOf(PidUtil.getPid()));

        HttpGet request = new HttpGet(uriBuilder.build());
        request.setConfig(requestConfig);
        // Send heartbeat request.
        CloseableHttpResponse response = client.execute(request);
        response.close();
        return true;
    }

    @Override
    public long intervalMs() {
        return 5000;
    }
}
