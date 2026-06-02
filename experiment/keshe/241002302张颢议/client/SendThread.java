import java.io.*;
import java.util.concurrent.*;
import javax.swing.*;

public class SendThread extends Thread {
    private PrintWriter writer;
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private boolean running = true;
    private Client client;

    public SendThread(PrintWriter writer, Client client) {
        this.writer = writer;
        this.client = client;
    }

    public void run() {
        while (running) {
            try {
                String message = messageQueue.take();
                writer.println(message);
                writer.flush();

                // 检查发送是否失败
                if (writer.checkError()) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(client,
                                "发送消息失败，网络连接已断开",
                                "错误",
                                JOptionPane.ERROR_MESSAGE);
                        client.disconnectPassive();
                    });
                    break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void sendMessage(String message) {
        if (!message.isEmpty() && running) {
            messageQueue.offer(message);
        }
    }

    public void stopThread() {
        running = false;
        interrupt();
    }
}