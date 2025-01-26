package chatserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static chatserver.ChatServer.LOCAL_HOST;
import static chatserver.ChatServer.DEFAULT_PORT;
import static org.junit.jupiter.api.Assertions.*;

public class ChatServerTest {

    private static final String TEST_MESSAGE_1    = "Test message 1";
    private static final String TEST_MESSAGE_2    = "Test message 2";
    private static final String TEST_MESSAGE_3    = "Test message 3";

    private ChatServer chatServer;

    @BeforeEach
    public void createAndStartChatServer() {
        this.chatServer = new ChatServer();
        Thread serverThread = new Thread(this.chatServer::start);
        serverThread.start();
    }

    @Test
    public void connectToServer() throws InterruptedException {
        Thread client1 = new Thread(() -> sendMessage(TEST_MESSAGE_1));
        Thread client2 = new Thread(() -> sendMessage(TEST_MESSAGE_2));
        Thread client3 = new Thread(() -> sendMessage(TEST_MESSAGE_3));
        client1.start();
        client2.start();
        client3.start();
        client1.join();
        client2.join();
        client3.join();
        Set<String> messages = chatServer.getMessages();
        assertTrue(messages.contains(TEST_MESSAGE_1));
        assertTrue(messages.contains(TEST_MESSAGE_2));
        assertTrue(messages.contains(TEST_MESSAGE_3));
    }

    private void sendMessage(String msg) {
        try (Socket socket = new Socket(LOCAL_HOST, DEFAULT_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
        } catch (IOException e) {
            System.out.println("Failed to send message to chat server");
        }
    }

}
