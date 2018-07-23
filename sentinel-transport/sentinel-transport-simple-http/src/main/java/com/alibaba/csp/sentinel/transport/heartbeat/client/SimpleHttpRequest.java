package com.alibaba.csp.sentinel.transport.heartbeat.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * Simple HTTP request representation.
 *
 * @author leyou
 */
public class SimpleHttpRequest {

    private InetSocketAddress socketAddress;
    private String requestPath = "";
    private int soTimeout = 3000;
    private Map<String, String> params;
    private Charset charset = Charset.forName(SentinelConfig.charset());

    public SimpleHttpRequest(InetSocketAddress socketAddress, String requestPath) {
        this.socketAddress = socketAddress;
        this.requestPath = requestPath;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public SimpleHttpRequest setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
        return this;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public SimpleHttpRequest setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public SimpleHttpRequest setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public SimpleHttpRequest setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public Charset getCharset() {
        return charset;
    }

    public SimpleHttpRequest setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public SimpleHttpRequest addParam(String key, String value) {
        if (StringUtil.isBlank(key)) {
            throw new IllegalArgumentException("Parameter key cannot be empty");
        }
        if (params == null) {
            params = new HashMap<String, String>();
        }
        params.put(key, value);
        return this;
    }
}
