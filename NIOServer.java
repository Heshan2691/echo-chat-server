import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;  // Add this import for ConcurrentHashMap

public class NIOServer {
    private static Map<SocketChannel, String> usernames = new ConcurrentHashMap<>();  // Username per channel
    private static Set<SocketChannel> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());  // Thread-safe set of clients
    private static Map<SocketChannel, StringBuilder> clientBuffers = new HashMap<>();  // Move to class field for access in methods

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 1234));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("NIO Server started on port 1234");

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    SocketChannel client = serverChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    clientBuffers.put(client, new StringBuilder());  // Use class field
                    clients.add(client);  // Track client
                    System.out.println("New client connected: " + client.getRemoteAddress());

                    // Step 1: Prompt for username (write immediately)
                    ByteBuffer promptBuffer = ByteBuffer.wrap("Enter your username:\n".getBytes());
                    client.write(promptBuffer);
                } else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    int bytesRead = client.read(buffer);
                    if (bytesRead == -1) {
                        handleDisconnect(client);
                        continue;
                    }
                    buffer.flip();
                    String message = new String(buffer.array(), 0, bytesRead).trim();

                    StringBuilder partialBuffer = clientBuffers.get(client);
                    partialBuffer.append(message).append("\n");
                    String fullMessage = partialBuffer.toString().trim();

                    // Handle username if first message (prompt response)
                    if (usernames.get(client) == null && fullMessage.contains("Enter your username:")) {
                        // Wait for next read? For simplicity, assume next read is username
                        continue;  // Defer to next readable event
                    } else if (usernames.get(client) == null) {
                        // This is the username
                        String username = fullMessage;
                        if (username.isEmpty()) username = "Anonymous";
                        usernames.put(client, username);
                        System.out.println("User '" + username + "' joined.");
                        broadcast(username + " has joined the chat.", client);  // Broadcast join
                        partialBuffer.setLength(0);  // Clear buffer
                        continue;
                    }

                    // Normal message
                    if ("exit".equalsIgnoreCase(fullMessage)) {
                        handleDisconnect(client);
                        continue;
                    }

                    System.out.println("Received from " + usernames.get(client) + ": " + fullMessage);
                    String broadcastMsg = usernames.get(client) + ": " + fullMessage;
                    broadcast(broadcastMsg, client);  // Broadcast to others
                    partialBuffer.setLength(0);  // Clear for next message
                }
                iterator.remove();
            }
        }
    }

    private static void broadcast(String message, SocketChannel sender) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap((message + "\n").getBytes());
        for (SocketChannel ch : clients) {
            if (ch != sender && ch.isConnected()) {  // Skip sender
                ch.write(writeBuffer);  // Send
                writeBuffer.rewind();  // Reset buffer position for next write
            }
        }
    }

    private static void handleDisconnect(SocketChannel client) throws IOException {
        clients.remove(client);
        String username = usernames.remove(client);
        if (username != null) {
            broadcast(username + " has left the chat.", null);  // Broadcast to all (sender null)
            System.out.println("User '" + username + "' disconnected.");
        }
        client.close();
        clientBuffers.remove(client);  // Now accessible as class field
    }
}