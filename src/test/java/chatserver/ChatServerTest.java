package chatserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.*;
import java.net.Socket;
import java.util.Set;

import static chatserver.ChatServer.LOCAL_HOST;
import static chatserver.ChatServer.DEFAULT_PORT;
import static org.junit.jupiter.api.Assertions.*;

public class ChatServerTest {

    private static final String TEST_MESSAGE_1    = "Test message 1";
    private static final String TEST_MESSAGE_2    = "Test message 2";
    private static final String TEST_MESSAGE_3    = "Test message 3";

    @BeforeAll
    public static void createAndStartChatServer() {
        Thread serverThread = new Thread(ChatServer::start);
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
        Set<String> messages = ChatServer.getMessages();
        assertTrue(messages.contains(TEST_MESSAGE_1));
        assertTrue(messages.contains(TEST_MESSAGE_2));
        assertTrue(messages.contains(TEST_MESSAGE_3));
    }

    @Test
    public void callAndResponse() {
        assertEquals(TEST_MESSAGE_1, echo(TEST_MESSAGE_1));
        assertEquals(TEST_MESSAGE_2, echo(TEST_MESSAGE_2));
        assertEquals(TEST_MESSAGE_3, echo(TEST_MESSAGE_3));
    }

    private void sendMessage(String msg) {
        try (Socket socket = new Socket(LOCAL_HOST, DEFAULT_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
        } catch (IOException e) {
            System.out.println("Failed to send message to chat server");
        }
    }

    private String echo(String msg) {
        try (Socket socket = new Socket(LOCAL_HOST, DEFAULT_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return in.readLine();
        } catch (IOException e) {
            System.out.println("Failed to send message to chat server");
            return null;
        }
    }

}
