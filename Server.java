import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;  // For thread-safe username map
import java.util.Map;  // For Map interface

public class Server {
    private static List<PrintWriter> clients = Collections.synchronizedList(new ArrayList<PrintWriter>());
    private static Map<PrintWriter, String> usernames = new ConcurrentHashMap<>();  // Username per client

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(1234)) {
            System.out.println("Server started on port 1234");
            while (true) {
                Socket client = server.accept();
                System.out.println("New client: " + client.getInetAddress());
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                clients.add(out);
                new Thread(new ClientHandler(client, out)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket, PrintWriter out) {
            this.socket = socket;
            this.out = out;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // Step 1: Prompt for and read username
                out.println("Enter your username:");  // Send prompt
                String username = in.readLine();  // Read response
                if (username == null || username.trim().isEmpty()) username = "Anonymous";
                usernames.put(out, username);  // Store in map
                System.out.println("User '" + username + "' joined.");

                // Step 2: Broadcast join message
                String joinMsg = username + " has joined the chat.";
                synchronized (clients) {
                    for (PrintWriter clientOut : clients) {
                        clientOut.println(joinMsg);
                    }
                }

                // Step 3: Message loop
                String line;
                while ((line = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(line)) break;
                    System.out.println("Received from " + username + ": " + line);
                    String broadcastMsg = username + ": " + line;  // Prepend username
                    synchronized (clients) {
                        for (PrintWriter clientOut : clients) {
                            clientOut.println(broadcastMsg);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Cleanup: Remove client and broadcast leave
                clients.remove(out);
                String username = usernames.remove(out);
                if (username != null) {
                    String leaveMsg = username + " has left the chat.";
                    synchronized (clients) {
                        for (PrintWriter clientOut : clients) {
                            clientOut.println(leaveMsg);
                        }
                    }
                    System.out.println("User '" + username + "' disconnected.");
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}