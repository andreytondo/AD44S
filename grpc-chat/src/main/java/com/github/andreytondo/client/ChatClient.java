package com.github.andreytondo.client;

import com.github.andreytondo.server.ChatServer;

import java.util.Scanner;

public class ChatClient {

    public static void main(String[] ignoredArgs) throws InterruptedException {
        String host = "127.0.0.1";
        int port = ChatServer.SERVER_PORT;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        ChatClientHandler client = new ChatClientHandler(host, port);
        try {
            client.joinGroup(username);
            client.startChat(username);
        } finally {
            client.shutdown();
        }
    }
}