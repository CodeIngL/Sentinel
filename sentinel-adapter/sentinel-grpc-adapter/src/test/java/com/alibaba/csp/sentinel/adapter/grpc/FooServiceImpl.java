package com.alibaba.csp.sentinel.adapter.grpc;

import com.alibaba.csp.sentinel.adapter.grpc.gen.FooRequest;
import com.alibaba.csp.sentinel.adapter.grpc.gen.FooResponse;
import com.alibaba.csp.sentinel.adapter.grpc.gen.FooServiceGrpc;

import io.grpc.stub.StreamObserver;

/**
 * Implementation of FooService defined in proto.
 */
class FooServiceImpl extends FooServiceGrpc.FooServiceImplBase {

    @Override
    public void sayHello(FooRequest request, StreamObserver<FooResponse> responseObserver) {
        String message = String.format("Hello %s! Your ID is %d.", request.getName(), request.getId());
        FooResponse response = FooResponse.newBuilder().setMessage(message).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void anotherHello(FooRequest request, StreamObserver<FooResponse> responseObserver) {
        String message = String.format("Good day, %s (%d)", request.getName(), request.getId());
        FooResponse response = FooResponse.newBuilder().setMessage(message).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
