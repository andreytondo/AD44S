package com.github.andreytondo.client.interfaces;

import java.util.Scanner;

public class ChatLogger implements ChatInterface {

    private static final String RESET = "\u001B[0m";
    private static final String USERNAME_COLOR = "\u001B[34m"; // Blue
    private static final String MESSAGE_COLOR = "\u001B[37m";  // White

    private final Scanner scanner;
    private final StringBuilder currentInput;

    public ChatLogger() {
        this.scanner = new Scanner(System.in);
        this.currentInput = new StringBuilder();
    }

    @Override
    public synchronized void displayMessage(String username, String message) {
        clearCurrentLine();
        System.out.println(USERNAME_COLOR + username + ": " + RESET + MESSAGE_COLOR + message + RESET);
        redrawTypingArea();
    }

    @Override
    public String getMessage() {
        redrawTypingArea();
        String input = scanner.nextLine();
        currentInput.setLength(0);
        return input;
    }

    private void clearCurrentLine() {
        System.out.print("\r");
        System.out.print("\033[2K");
    }

    private void redrawTypingArea() {
        System.out.print("Você: " + currentInput);
    }
}
