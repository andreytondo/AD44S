package com.github.andreytondo;

import dk.adamino.grpc.chat.ChatServiceGrpc;
import dk.adamino.grpc.chat.ChatServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChatClient {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", ChatServer.SERVER_PORT)
                .usePlaintext()
                .build();

        ChatServiceGrpc.ChatServiceStub stub = ChatServiceGrpc.newStub(channel);

        CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<ChatServiceOuterClass.ChatMessage> requestObserver = stub.chat(new StreamObserver<>() {
            @Override
            public void onNext(ChatServiceOuterClass.ChatMessage message) {
                System.out.println("Received: " + message.getUser() + ": " + message.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Chat ended.");
                finishLatch.countDown();
            }
        });

        requestObserver.onNext(ChatServiceOuterClass.ChatMessage.newBuilder().setUser("User1").setMessage("Hello!").build());
        requestObserver.onNext(ChatServiceOuterClass.ChatMessage.newBuilder().setUser("User2").setMessage("Hi!").build());

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter messages (type 'exit' to quit):");
        while (true) {
            String userInput = scanner.nextLine();
            if ("exit".equalsIgnoreCase(userInput)) {
                requestObserver.onCompleted();
                break;
            }
            requestObserver.onNext(ChatServiceOuterClass.ChatMessage.newBuilder().setUser("Client").setMessage(userInput).build());
        }

        // Wait for the server to close the connection
        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
            System.err.println("The chat did not finish in time.");
        }

        channel.shutdownNow();
    }
}