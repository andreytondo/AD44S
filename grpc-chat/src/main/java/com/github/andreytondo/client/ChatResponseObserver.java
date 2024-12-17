package com.github.andreytondo.client;

import com.github.andreytondo.ChatServiceOuterClass;
import com.github.andreytondo.client.interfaces.ChatInterface;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class ChatResponseObserver implements StreamObserver<ChatServiceOuterClass.ChatMessage> {

    private final CountDownLatch latch;
    private final ChatInterface chatInterface;

    public ChatResponseObserver(CountDownLatch latch, ChatInterface chatInterface) {
        this.latch = latch;
        this.chatInterface = chatInterface;
    }

    @Override
    public void onNext(ChatServiceOuterClass.ChatMessage chatMessage) {
        chatInterface.displayMessage(chatMessage.getUser(), chatMessage.getMessage());
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