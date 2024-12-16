package com.github.andreytondo.client;

import com.github.andreytondo.ChatServiceOuterClass;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class ChatResponseObserver implements StreamObserver<ChatServiceOuterClass.ChatMessage> {

    private final CountDownLatch latch;

    public ChatResponseObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(ChatServiceOuterClass.ChatMessage chatMessage) {
        System.out.println("[" + chatMessage.getUser() + "]: " + chatMessage.getMessage());
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Chat closed");
        latch.countDown();
    }
}