package com.github.andreytondo.client;

import static com.github.andreytondo.ChatServiceGrpc.*;
import static com.github.andreytondo.ChatServiceOuterClass.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChatClientHandler {

    private final ManagedChannel channel;
    private final ChatServiceStub asyncStub;
    private final ChatServiceBlockingStub blockingStub;

    public ChatClientHandler(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.asyncStub = newStub(channel);
        this.blockingStub = newBlockingStub(channel);
    }

    public void joinGroup(String username) {
        JoinGroupRequest request = JoinGroupRequest.newBuilder()
                .setUser(username)
                .build();
        JoinGroupResponse response = blockingStub.joinGroup(request);
        System.out.println("Joined group: " + response.getStatus());
    }

    public void startChat(String username) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<ChatMessage> requestObserver = asyncStub.chat(new ChatResponseObserver(latch));

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    requestObserver.onCompleted();
                    break;
                }
                ChatMessage chatMessage = ChatMessage.newBuilder()
                        .setUser(username)
                        .setMessage(message)
                        .build();
                requestObserver.onNext(chatMessage);
            }
        }).start();

        latch.await(1, TimeUnit.HOURS);
    }

    public void shutdown() {
        channel.shutdownNow();
    }
}
