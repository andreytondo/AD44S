package client;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 2025;
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("Iniciando cliente de chat...");

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("[✓] Conectado ao servidor de chat.");

            // Thread para receber mensagens do servidor
            Thread readerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.print("\r");
                        System.out.print("\033[2K");
                        System.out.println(formatMessage("Servidor", serverMessage));
                        System.out.print("\r");
                        System.out.print("\033[2K");
                        System.out.print("Você: ");
                    }
                } catch (IOException e) {
                    System.err.println("[Erro] Conexão com o servidor perdida.");
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            // Loop para envio de mensagens
            while (true) {
                System.out.print("Você: ");
                String userMessage = scanner.nextLine();
                if (userMessage.equalsIgnoreCase("/sair")) {
                    System.out.println("[!] Desconectando do chat...");
                    break;
                }
                out.println(userMessage);
            }

        } catch (IOException e) {
            System.err.println("[Erro] Falha ao conectar-se ao servidor: " + e.getMessage());
        }

        System.out.println("Cliente encerrado.");
    }

    private static String formatMessage(String sender, String message) {
        return String.format("[%s] %s: %s", TIMESTAMP_FORMAT.format(new Date()), sender, message);
    }
}