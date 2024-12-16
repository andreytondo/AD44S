package com.github.andreytondo.server;

import com.github.andreytondo.ChatServiceGrpc;
import static com.github.andreytondo.ChatServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        ChatServer.OBSERVERS.add(responseObserver);
        return new ChatStreamObserver(responseObserver, ChatServer.OBSERVERS);
    }

    @Override
    public void joinGroup(JoinGroupRequest request, StreamObserver<JoinGroupResponse> responseObserver) {
        JoinGroupResponse response = JoinGroupResponse.newBuilder()
                .setStatus("OK")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
