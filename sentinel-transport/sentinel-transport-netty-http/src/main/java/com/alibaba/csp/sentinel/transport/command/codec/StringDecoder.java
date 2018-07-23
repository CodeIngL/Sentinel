package com.alibaba.csp.sentinel.transport.command.codec;

import java.nio.charset.Charset;

import com.alibaba.csp.sentinel.config.SentinelConfig;

/**
 * Decodes from a byte array to string.
 *
 * @author Eric Zhao
 */
public class StringDecoder implements Decoder<String> {

    @Override
    public boolean canDecode(Class<?> clazz) {
        return String.class.isAssignableFrom(clazz);
    }

    @Override
    public String decode(byte[] bytes) throws Exception {
        return decode(bytes, Charset.forName(SentinelConfig.charset()));
    }

    @Override
    public String decode(byte[] bytes, Charset charset) {
        if (bytes == null || bytes.length <= 0) {
            throw new IllegalArgumentException("Bad byte array");
        }
        return new String(bytes, charset);
    }
}
