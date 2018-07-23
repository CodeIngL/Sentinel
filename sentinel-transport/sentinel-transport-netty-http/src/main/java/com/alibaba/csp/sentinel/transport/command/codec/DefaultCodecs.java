package com.alibaba.csp.sentinel.transport.command.codec;

/**
 * Caches default encoders and decoders.
 *
 * @author Eric Zhao
 */
final class DefaultCodecs {

    public static final Encoder<String> STRING_ENCODER = new StringEncoder();

    public static final Decoder<String> STRING_DECODER = new StringDecoder();

    private DefaultCodecs() {}
}
