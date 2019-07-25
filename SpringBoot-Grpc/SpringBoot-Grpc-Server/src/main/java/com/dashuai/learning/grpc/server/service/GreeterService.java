package com.dashuai.learning.grpc.server.service;

import com.dashuai.learning.grpc.lib.proto.GreeterGrpc;
import com.dashuai.learning.grpc.lib.proto.GreeterOuterClass;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;

/**
 * Greeter service
 * <p/>
 * Created in 2019.07.22
 * <p/>
 *
 * @author Liaozihong
 */
@Slf4j
@GrpcService(GreeterOuterClass.class)
public class GreeterService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(GreeterOuterClass.HelloRequest request, StreamObserver<GreeterOuterClass.HelloReply> responseObserver) {
        String message = "Hello " + request.getName();
        final GreeterOuterClass.HelloReply.Builder replyBuilder = GreeterOuterClass.HelloReply.newBuilder().setMessage(message);
        responseObserver.onNext(replyBuilder.build());
        responseObserver.onCompleted();
        System.out.println("Returning " + message);
    }
}
