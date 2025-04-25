package mess;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("🔌 Server đang chạy tại port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("✅ Client kết nối: " + socket);
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("📨 Nhận: " + message);
                    broadcast(message);
                }
            } catch (IOException e) {
                System.out.println("❌ Client ngắt kết nối: " + socket);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                clients.remove(this);
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.out.println(message);
                }
            }
        }
    }
}
