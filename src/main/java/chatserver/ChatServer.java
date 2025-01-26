package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {

    private static final Set<String> MESSAGES = new HashSet<>();

    protected static final String LOCAL_HOST  = "localhost";
    protected static final int DEFAULT_PORT   = 12345;

    public static void start() {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException ignored) {}
    }

    public static Set<String> getMessages() { return MESSAGES; }

    private static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String msg = in.readLine();
                synchronized (MESSAGES) {
                    MESSAGES.add(msg);
                }
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(msg);
           } catch (IOException ignored) {}
        }
    }
}
