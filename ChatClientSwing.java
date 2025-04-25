package mess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClientSwing extends JFrame {
    private JTextField ipField, portField, inputField;
    private JTextArea messageArea;
    private JButton connectButton, sendButton;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClientSwing() {
        setTitle("💬 Chat Client (Swing)");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel IP + Port + Kết nối
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(240, 240, 240));
        ipField = new JTextField("localhost", 10);
        portField = new JTextField("12345", 5);
        connectButton = new JButton("Kết nối");
        connectButton.setBackground(new Color(0, 123, 255));
        connectButton.setForeground(Color.WHITE);
        topPanel.add(new JLabel("IP:"));
        topPanel.add(ipField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(connectButton);
        add(topPanel, BorderLayout.NORTH);

        // Hiển thị tin nhắn
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(245, 245, 245));
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Nhập tin + nút Gửi
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        sendButton = new JButton("Gửi");
        sendButton.setBackground(new Color(0, 123, 255));
        sendButton.setForeground(Color.WHITE);
        inputField.setEnabled(false);
        sendButton.setEnabled(false);
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Sự kiện nút
        connectButton.addActionListener(e -> connectToServer());
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        setVisible(true);
    }

    private void connectToServer() {
        String ip = ipField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());

        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            showMessage("[Đã kết nối tới server]");
            connectButton.setEnabled(false);
            ipField.setEnabled(false);
            portField.setEnabled(false);
            inputField.setEnabled(true);
            sendButton.setEnabled(true);

            // Nhận tin nhắn
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        messageArea.append("Bạn khác: " + msg + "\n");
                    }
                } catch (IOException e) {
                    showMessage("[Mất kết nối]");
                }
            }).start();

        } catch (IOException e) {
            showMessage("[Lỗi] Không thể kết nối: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            out.println(msg);
            messageArea.append("Bạn: " + msg + "\n");
            inputField.setText("");
        }
    }

    private void showMessage(String msg) {
        messageArea.append(msg + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClientSwing::new);
    }
}
