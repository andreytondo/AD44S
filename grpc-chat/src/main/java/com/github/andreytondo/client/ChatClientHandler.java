package com.github.andreytondo.client;

import static com.github.andreytondo.ChatServiceGrpc.*;
import static com.github.andreytondo.ChatServiceOuterClass.*;

import com.github.andreytondo.client.interfaces.ChatInterface;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChatClientHandler {

    private final ManagedChannel channel;
    private final ChatServiceStub asyncStub;
    private final ChatServiceBlockingStub blockingStub;
    private final ChatInterface chatInterface;

    public ChatClientHandler(String host, int port, ChatInterface chatInterface) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.asyncStub = newStub(channel);
        this.blockingStub = newBlockingStub(channel);
        this.chatInterface = chatInterface;
    }

    public void joinGroup(String username) {
        JoinGroupRequest request = JoinGroupRequest.newBuilder()
                .setUser(username)
                .build();
        JoinGroupResponse response = blockingStub.joinGroup(request);
        System.out.println("Conexão " + response.getStatus());
        System.out.println("Digite 'exit' para sair do chat");
    }

    public void startChat(String username) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<ChatMessage> requestObserver = asyncStub.chat(new ChatResponseObserver(latch, chatInterface));

        new Thread(() -> {
            while (true) {
                String message = chatInterface.getMessage();
                if (message.equalsIgnoreCase("exit")) {
                    requestObserver.onCompleted();
                    break;
                }
                sendMessage(requestObserver, username, message);
            }
        }).start();

        latch.await(1, TimeUnit.HOURS);
    }

    private void sendMessage(StreamObserver<ChatMessage> observer, String username, String message) {
        ChatMessage chatMessage = ChatMessage.newBuilder()
                .setUser(username)
                .setMessage(message)
                .build();
        observer.onNext(chatMessage);
    }

    public void shutdown() {
        channel.shutdownNow();
    }
}
