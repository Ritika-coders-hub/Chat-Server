import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Server {
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;
    JFrame frame;
    JTextArea textArea;
    JTextField textField;
    JButton sendButton;
    JPanel panel;

    public Server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            createGUI();
            handleEvents();

            startReading();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        textField = new JTextField(20);
        sendButton = new JButton("Send");

        panel = new JPanel();
        panel.add(textField);
        panel.add(sendButton);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void handleEvents() {
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                textArea.append("Server: " + message + "\n");
                out.println(message);
                textField.setText("");
            }
        });

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            while (true) {
                try {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(frame, "Client has left the chat");
                        break;
                    }
                    textArea.append("Client: " + msg + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r1).start();
    }
    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            while (true) {
                try {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is server... going to start server");
        new Server();
    }
}

