package com.alibaba.csp.sentinel.transport.heartbeat.client;

import java.nio.charset.Charset;
import java.util.Map;

import com.alibaba.csp.sentinel.config.SentinelConfig;

/**
 * Simple HTTP response representation.
 *
 * @author leyou
 */
public class SimpleHttpResponse {

    private Charset charset = Charset.forName(SentinelConfig.charset());

    private String statusLine;
    private int statusCode;
    private Map<String, String> headers;
    private byte[] body;

    public SimpleHttpResponse(String statusLine, Map<String, String> headers) {
        this.statusLine = statusLine;
        this.headers = headers;
    }

    public SimpleHttpResponse(String statusLine, Map<String, String> headers, byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    private void parseCharset() {
        String contentType = getHeader("Content-Type");
        for (String str : contentType.split(" ")) {
            if (str.toLowerCase().startsWith("charset=")) {
                charset = Charset.forName(str.split("=")[1]);
            }
        }
    }

    private void parseCode() {
        this.statusCode = Integer.parseInt(statusLine.split(" ")[1]);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public Integer getStatusCode() {
        if (statusCode == 0) {
            parseCode();
        }
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Get header of the key ignoring case.
     *
     * @param key header key
     * @return header value
     */
    public String getHeader(String key) {
        if (headers == null) {
            return null;
        }
        String value = headers.get(key);
        if (value != null) {
            return value;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public String getBodyAsString() {
        parseCharset();
        return new String(body, charset);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(statusLine)
            .append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            buf.append(entry.getKey()).append(": ").append(entry.getValue())
                .append("\r\n");
        }
        buf.append("\r\n");
        buf.append(getBodyAsString());
        return buf.toString();
    }
}
