package com.github.andreytondo;

import dk.adamino.grpc.chat.ChatServiceGrpc;
import dk.adamino.grpc.chat.ChatServiceOuterClass;
import io.grpc.stub.StreamObserver;

public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    @Override
    public StreamObserver<ChatServiceOuterClass.ChatMessage> chat(StreamObserver<ChatServiceOuterClass.ChatMessage> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ChatServiceOuterClass.ChatMessage value) {
                System.out.println("Received message: " + value.getMessage());
                responseObserver.onNext(ChatServiceOuterClass.ChatMessage.newBuilder().setMessage("Server received message: " + value.getMessage()).build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
