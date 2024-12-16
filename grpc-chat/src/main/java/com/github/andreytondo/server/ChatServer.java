package com.github.andreytondo.server;

import static com.github.andreytondo.ChatServiceOuterClass.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    public static final List<StreamObserver<ChatMessage>> OBSERVERS = new CopyOnWriteArrayList<>();

    public static final int SERVER_PORT = 9090;

    public static void main(String[] ignoredArgs) throws Exception {
        Server server = ServerBuilder.forPort(SERVER_PORT)
                .addService(new ChatServiceImpl())
                .build();

        server.start();
        server.awaitTermination();
    }
}
