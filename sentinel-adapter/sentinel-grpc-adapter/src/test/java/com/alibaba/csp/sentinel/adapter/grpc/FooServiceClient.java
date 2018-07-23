package com.alibaba.csp.sentinel.adapter.grpc;

import java.util.concurrent.TimeUnit;

import com.alibaba.csp.sentinel.adapter.grpc.gen.FooRequest;
import com.alibaba.csp.sentinel.adapter.grpc.gen.FooResponse;
import com.alibaba.csp.sentinel.adapter.grpc.gen.FooServiceGrpc;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * A simple wrapped gRPC client for FooService.
 *
 * @author Eric Zhao
 */
final class FooServiceClient {

    private final ManagedChannel channel;
    private final FooServiceGrpc.FooServiceBlockingStub blockingStub;

    FooServiceClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build();
        this.blockingStub = FooServiceGrpc.newBlockingStub(this.channel);
    }

    FooServiceClient(String host, int port, ClientInterceptor interceptor) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(interceptor)
            .build();
        this.blockingStub = FooServiceGrpc.newBlockingStub(this.channel);
    }

    FooResponse sayHello(FooRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        return blockingStub.sayHello(request);
    }

    FooResponse anotherHello(FooRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        return blockingStub.anotherHello(request);
    }

    void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
    }
}
