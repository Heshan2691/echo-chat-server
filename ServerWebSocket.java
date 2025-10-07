import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerWebSocket extends WebSocketServer {
    private static Map<WebSocket, String> usernames = new ConcurrentHashMap<>();  // Username per connection
    private static Set<WebSocket> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());  // Thread-safe set

    public ServerWebSocket(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New client connected: " + conn.getRemoteSocketAddress());
        clients.add(conn);  // Track client
        conn.send("Enter your username:");  // Prompt for username
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        String username = usernames.remove(conn);
        if (username != null) {
            broadcast(username + " has left the chat.", conn);
            System.out.println("User '" + username + "' disconnected.");
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Handle username if first message
        if (usernames.get(conn) == null) {
            String username = message.trim();
            if (username.isEmpty()) username = "Anonymous";
            usernames.put(conn, username);
            System.out.println("User '" + username + "' joined.");
            broadcast(username + " has joined the chat.", conn);
            conn.send("Welcome, " + username + "! Type messages below. (Use /exit to quit)");
            return;
        }

        // Handle exit
        if ("/exit".equalsIgnoreCase(message.trim())) {
            conn.close();
            return;
        }

        // Normal message
        System.out.println("Received from " + usernames.get(conn) + ": " + message);
        String broadcastMsg = usernames.get(conn) + ": " + message;
        broadcast(broadcastMsg, conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server started on port 1234");
    }

    private void broadcast(String message, WebSocket sender) {
        for (WebSocket client : clients) {
            if (client != sender && client.isOpen()) {
                client.send(message);
            }
        }
    }

    public static void main(String[] args) {
        ServerWebSocket server = new ServerWebSocket(1234);
        server.run();  // Starts the server (uses NIO internally)
    }
}