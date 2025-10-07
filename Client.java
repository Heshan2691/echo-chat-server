import java.io.*;
import java.net.*;
import java.util.Scanner;  // For user input

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1234)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  // Write to server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // Read from server
            Scanner sc = new Scanner(System.in);  // Read from console

            // Step 1: Read username prompt from server and send username
            String prompt = in.readLine();  // Expect "Enter your username:"
            if (prompt != null && prompt.contains("username")) {
                System.out.println(prompt);  // Show prompt to user
                String username = sc.nextLine().trim();  // Get username from user
                if (username.isEmpty()) username = "Anonymous";  // Default if empty
                out.println(username);  // Send to server
                System.out.println("Welcome, " + username + "! Type 'exit' to quit.");
            }

            // Step 2: Normal message loop
            String line;
            while (!"exit".equalsIgnoreCase(line = sc.nextLine())) {
                out.println(line);  // Send message to server
                System.out.println("Server: " + in.readLine());  // Print broadcast (now with usernames)
            }
            out.println("exit");  // Notify server of exit
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}