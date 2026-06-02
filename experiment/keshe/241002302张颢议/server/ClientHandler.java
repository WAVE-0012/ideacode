import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private Server server;
    private BufferedReader reader;
    private PrintWriter writer;
    private String nickname;
    private volatile boolean connected = true;  // 添加volatile保证可见性
    private boolean isKicked = false;
    private volatile boolean isRunning = true;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            socket.setSoTimeout(3000);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            connected = false;
            isRunning = false;
        }
    }

    public void run() {
        try {
            nickname = reader.readLine();

            if (nickname == null || nickname.trim().isEmpty() || nickname.equals("管理员")) {
                writer.println("ERROR:昵称不符合规范，或者昵称在群聊里已存在，请尝试其他昵称");
                close();
                return;
            }

            for (ClientHandler ch : server.getClients()) {
                if (ch != this && ch.getNickname().equals(nickname)) {
                    writer.println("ERROR:昵称不符合规范，或者昵称在群聊里已存在，请尝试其他昵称");
                    close();
                    return;
                }
            }

            writer.println("OK");
            server.addClient(this);
            server.broadcast("【" + server.getCurrentTime() + "," + nickname + "】：【进入了群聊室】", this);

            String msg;
            // 修改循环条件，同时检查两个标志
            while (connected && isRunning) {
                try {
                    msg = reader.readLine();
                    if (msg == null) {
                        break;
                    }
                    if (msg.equalsIgnoreCase("exit")) {
                        break;
                    }
                    server.broadcast("【" + server.getCurrentTime() + "," + nickname + "】:" + msg, this);
                } catch (SocketTimeoutException e) {
                    // 超时，继续循环检查标志
                    continue;
                } catch (IOException e) {
                    // IO异常，退出循环
                    break;
                }
            }
        } catch (IOException e) {
            // 连接异常
        } finally {
            connected = false;
            isRunning = false;
            server.removeClient(this, isKicked);
            close();
        }
    }

    public void kickOut() {
        isKicked = true;
        connected = false;
        isRunning = false;
        writer.println("KICKED");
        writer.flush();
        close();
    }

    public void sendMessage(String msg) {
        if (writer != null && connected) {
            writer.println(msg);
            writer.flush();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void close() {
        connected = false;
        isRunning = false;
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            // 中断线程，让阻塞的readLine立即返回
            interrupt();
        } catch (IOException e) {
            // 忽略关闭时的异常
        }
    }
}