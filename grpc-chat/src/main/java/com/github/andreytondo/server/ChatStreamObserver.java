package com.github.andreytondo.server;

import io.grpc.stub.StreamObserver;

import java.util.List;
import static com.github.andreytondo.ChatServiceOuterClass.*;

public class ChatStreamObserver implements StreamObserver<ChatMessage> {

    private final StreamObserver<ChatMessage> responseObserver;
    private final List<StreamObserver<ChatMessage>> groupObservers;

    public ChatStreamObserver(
            StreamObserver<ChatMessage> responseObserver,
            List<StreamObserver<ChatMessage>> groupObservers) {
        this.responseObserver = responseObserver;
        this.groupObservers = groupObservers;
    }

    @Override
    public void onNext(ChatMessage chatMessage) {
        for (StreamObserver<ChatMessage> observer : groupObservers) {
            if (observer == responseObserver) {
                continue;
            }
            observer.onNext(chatMessage);
        }
    }

    @Override
    public void onError(Throwable t) {
        groupObservers.remove(responseObserver);
    }

    @Override
    public void onCompleted() {
        groupObservers.remove(responseObserver);
        responseObserver.onCompleted();
    }
}
