package com.alibaba.csp.sentinel.transport.command.codec;

import java.nio.charset.Charset;

/**
 * The decoder decodes bytes into an object of type {@code <R>}.
 *
 * @param <R> target type
 * @author Eric Zhao
 */
public interface Decoder<R> {

    /**
     * Check whether the decoder supports the given target type.
     *
     * @param clazz type of the class
     * @return {@code true} if supported, {@code false} otherwise
     */
    boolean canDecode(Class<?> clazz);

    /**
     * Decode the given byte array into an object of type {@code R} with the default charset.
     *
     * @param bytes raw byte buffer
     * @return the decoded target object
     * @throws Exception error occurs when decoding the object (e.g. IO fails)
     */
    R decode(byte[] bytes) throws Exception;

    /**
     * Decode the given byte array into an object of type {@code R} with the given charset.
     *
     * @param bytes raw byte buffer
     * @param charset the charset
     * @return the decoded target object
     * @throws Exception error occurs when decoding the object (e.g. IO fails)
     */
    R decode(byte[] bytes, Charset charset) throws Exception;
}
