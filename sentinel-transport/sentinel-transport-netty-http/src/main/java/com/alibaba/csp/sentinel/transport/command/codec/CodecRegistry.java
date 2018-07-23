package com.alibaba.csp.sentinel.transport.command.codec;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eric Zhao
 */
public final class CodecRegistry {

    private final List<Encoder<?>> encoderList = new ArrayList<Encoder<?>>();
    private final List<Decoder<?>> decoderList = new ArrayList<Decoder<?>>();

    public CodecRegistry() {
        // Register default codecs.
        registerEncoder(DefaultCodecs.STRING_ENCODER);

        registerDecoder(DefaultCodecs.STRING_DECODER);
    }

    public void registerEncoder(Encoder<?> encoder) {
        encoderList.add(encoder);
    }

    public void registerDecoder(Decoder<?> decoder) {
        decoderList.add(decoder);
    }

    public List<Encoder<?>> getEncoderList() {
        return encoderList;
    }

    public List<Decoder<?>> getDecoderList() {
        return decoderList;
    }

    public void reset() {
        encoderList.clear();
        decoderList.clear();
    }
}
