import java.io.*;
import java.net.*;
import javax.swing.*;

public class ReceiveThread extends Thread {
    private Socket socket;
    private JTextArea chatArea;
    private BufferedReader reader;
    private Client client;
    private boolean running = true;

    public ReceiveThread(Socket socket, JTextArea chatArea, Client client) {
        this.socket = socket;
        this.chatArea = chatArea;
        this.client = client;
        try {
            socket.setSoTimeout(3000);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            running = false;
        }
    }

    public void run() {
        while (running) {
            try {
                String msg = reader.readLine();
                if (msg == null) {
                    SwingUtilities.invokeLater(() -> {
                        if (!client.isManualExit()) {
                            JOptionPane.showMessageDialog(client, "服务已关停，群聊室停止营业", "消息", JOptionPane.INFORMATION_MESSAGE);
                            client.disconnectPassive();
                        } else {
                            client.disconnectPassive();
                        }
                    });
                    break;
                }

                // 服务器关停信号
                if (msg != null && (msg.equals("SERVER_STOPPED") || msg.equals("DISCONNECT"))) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(client, "服务已关停，群聊室停止营业", "消息", JOptionPane.INFORMATION_MESSAGE);
                        client.disconnectPassive();
                    });
                    break;
                }

                // 被踢出
                if (msg != null && msg.equals("KICKED")) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(client, "由于你违反群聊纪律，被踢出聊天群", "消息", JOptionPane.INFORMATION_MESSAGE);
                        client.disconnectPassive();
                    });
                    break;
                }

                final String finalMsg = msg;
                SwingUtilities.invokeLater(() -> chatArea.append(finalMsg + "\n"));
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                if (running) {
                    SwingUtilities.invokeLater(() -> {
                        if (!client.isManualExit()) {
                            JOptionPane.showMessageDialog(client, "服务已关停，群聊室停止营业", "消息", JOptionPane.INFORMATION_MESSAGE);
                            client.disconnectPassive();
                        } else {
                            client.disconnectPassive();
                        }
                    });
                }
                break;
            }
        }
    }

    public void stopThread() {
        running = false;
        try {
            if (reader != null) reader.close();
        } catch (IOException e) {}
        interrupt();
    }
}