package com.github.andreytondo;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ChatServer {

    public static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(SERVER_PORT)
                .addService(new ChatServiceImpl())
                .build();

        server.start();
        server.awaitTermination();
    }
}
