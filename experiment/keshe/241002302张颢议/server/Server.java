import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 聊天室服务器端主程序
 * 功能：提供图形化界面，启动/停止服务，管理在线客户端，广播消息，踢人等
 * 作者：241002302张颢议
 * ┌─────────────────────────────────────────────────────────────┐
 * │                      Server (服务器主类)                      │
 * ├─────────────────────────────────────────────────────────────┤
 * │  • UI界面管理             —— 构造方法 + 组件定义                 │
 * │  • 启动/停止服务          —— startServer()、stopServer()       │
 * │  • 管理所有客户端连接      —— clients列表 + add/remove           │
 * │  • 广播消息给所有客户端    —— broadcast()                       │
 * │  • 踢人功能               —— kickUser()                       │
 * ├─────────────────────────────────────────────────────────────┤
 * │  内部线程:                                                    │
 * │  • WelcomeThread  - 迎宾线程，接受新客户端连接                    │
 * │  • PatrolThread   - 巡检线程，检测断开连接的客户端                 │
 * │  • ClientHandler  - 每个客户端一个线程，处理该客户端的通信          │
 * └─────────────────────────────────────────────────────────────┘
 */
public class Server extends JFrame {
    // ========== UI组件 ==========
    private JTextField portField;            // 端口输入框
    private JButton startBtn;                // 启动服务按钮
    private JButton stopBtn;                 // 停止服务按钮
    private JButton kickBtn;                 // 踢人按钮
    private JTextField kickNameField;        // 踢人昵称输入框
    private JTextArea clientListArea;        // 在线客户端列表显示区域
    private JTextArea logArea;               // 聊天日志显示区域
    private JTextArea inputArea;             // 管理员发送消息输入框

    // ========== 网络服务组件 ==========
    private ServerSocket serverSocket;        // 服务器Socket，监听客户端连接
    private volatile boolean isRunning = false; // 服务运行状态标记（volatile保证多线程可见）
    private List<ClientHandler> clients = new ArrayList<>(); // 保存所有在线客户端处理器

    // ========== 后台线程 ==========
    private PatrolThread patrolThread;       // 客户端在线状态巡检线程
    private WelcomeThread welcomeThread;     // 监听新客户端连接的迎宾线程

    /**
     * 构造方法：初始化服务器图形界面
     */
    public Server() {
        setTitle("全民大讨论聊天室服务器端--241002302张颢议");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 窗口居中
        setLayout(new BorderLayout(0, 5));

        // ========== 顶部工具栏：端口、启动、停止、踢人 ==========
        JPanel toolBar = new JPanel(new GridLayout(1, 5, 8, 0));
        toolBar.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        portField = new JTextField("3083");
        portField.setHorizontalAlignment(JTextField.CENTER);

        startBtn = new JButton("启动服务");
        stopBtn = new JButton("关停服务");
        stopBtn.setEnabled(false); // 默认停止按钮不可用
        kickBtn = new JButton("踢出一位聊客：");
        kickBtn.setEnabled(false); // 默认踢人按钮不可用
        kickNameField = new JTextField();
        kickNameField.setHorizontalAlignment(JTextField.CENTER);

        toolBar.add(portField);
        toolBar.add(startBtn);
        toolBar.add(stopBtn);
        toolBar.add(kickBtn);
        toolBar.add(kickNameField);

        // ========== 在线聊客列表显示区域 ==========
        JPanel clientPanel = new JPanel(new BorderLayout(5, 0));
        clientPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel clientLabel = new JLabel("聊客列表：");
        clientLabel.setPreferredSize(new Dimension(70, 25));

        clientListArea = new JTextArea(2, 30);
        clientListArea.setText("暂无聊客");
        clientListArea.setEditable(false); // 不可编辑
        clientListArea.setFont(new Font("宋体", Font.PLAIN, 12));
        clientListArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        clientListArea.setLineWrap(true);
        clientListArea.setWrapStyleWord(false);

        JScrollPane clientScrollPane = new JScrollPane(clientListArea);
        clientScrollPane.setBorder(BorderFactory.createEmptyBorder());

        clientPanel.add(clientLabel, BorderLayout.WEST);
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);

        // ========== 聊天记录日志区域 ==========
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("宋体", Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // ========== 底部管理员消息输入区域 ==========
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        inputArea = new JTextArea(3, 30);
        inputArea.setFont(new Font("宋体", Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JButton sendBtn = new JButton("发送");
        sendBtn.setPreferredSize(new Dimension(70, 50));
        // 管理员发送消息事件
        sendBtn.addActionListener(e -> {
            String msg = inputArea.getText().trim();
            if (!msg.isEmpty()) {
                broadcast("【" + getCurrentTime() + "，管理员】：" + msg, null);
                inputArea.setText(""); // 清空输入框
            }
        });

        bottomPanel.add(inputScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);

        // ========== 界面组装 ==========
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(toolBar, BorderLayout.NORTH);
        northContainer.add(clientPanel, BorderLayout.CENTER);

        add(northContainer, BorderLayout.NORTH);
        add(logScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 按钮绑定事件
        startBtn.addActionListener(e -> startServer());
        stopBtn.addActionListener(e -> stopServer());
        kickBtn.addActionListener(e -> kickUser());

        setVisible(true); // 显示窗口
    }

    /**
     * 获取当前系统时间（HH:mm:ss格式）
     */
    public String getCurrentTime() {
        return new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
    }

    /**
     * 更新在线客户端列表显示
     * 把所有在线用户昵称拼接后展示在界面上
     */
    private void updateClientList() {
        StringBuilder sb = new StringBuilder();
        if (clients.isEmpty()) {
            sb.append("暂无聊客");
        } else {
            for (int i = 0; i < clients.size(); i++) {
                if (i > 0) sb.append("，");
                sb.append(clients.get(i).getNickname());
            }
        }
        clientListArea.setText(sb.toString());
    }

    /**
     * 启动服务器服务
     * 1. 校验端口合法性
     * 2. 创建ServerSocket
     * 3. 启动迎宾线程和巡检线程
     * 4. 更新按钮状态
     */
    private void startServer() {
        int port;
        try {
            port = Integer.parseInt(portField.getText());
            if (port < 1024 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "端口必须是1024~65535之间的整数", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;

            // 更新UI按钮状态
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            kickBtn.setEnabled(true);
            portField.setEnabled(false);

            JOptionPane.showMessageDialog(this,
                    "服务已启动，可以接入客户端连接了",
                    "消息",
                    JOptionPane.INFORMATION_MESSAGE);

            // 启动后台线程
            welcomeThread = new WelcomeThread();
            welcomeThread.start();
            patrolThread = new PatrolThread(this);
            patrolThread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "启动失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 停止服务器服务
     * 1. 关闭运行标记
     * 2. 广播关闭通知
     * 3. 断开所有客户端
     * 4. 关闭ServerSocket
     * 5. 停止所有后台线程
     * 6. 恢复UI初始状态
     */
    private void stopServer() {
        if (!isRunning) {
            return;
        }

        // 1. 设置停止标记
        isRunning = false;

        // 2. 广播服务器关闭消息
        String shutdownMsg = "【" + getCurrentTime() + "，管理员】：【暂停服务，大家散了吧】";
        broadcast(shutdownMsg, null);

        // 等待消息发送完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3. 关闭所有客户端连接
        List<ClientHandler> clientsToRemove = new ArrayList<>(clients);
        for (ClientHandler ch : clientsToRemove) {
            ch.sendMessage("SERVER_STOPPED");
            ch.close();
            try {
                ch.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        clients.clear();
        updateClientList();

        // 4. 关闭服务器Socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            serverSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 5. 停止巡检线程
        if (patrolThread != null) {
            patrolThread.stopThread();
            try {
                patrolThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            patrolThread = null;
        }

        // 6. 等待迎宾线程结束
        if (welcomeThread != null) {
            try {
                welcomeThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            welcomeThread = null;
        }

        // 7. 恢复UI按钮状态
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        kickBtn.setEnabled(false);
        portField.setEnabled(true);
        kickNameField.setEnabled(true);

        JOptionPane.showMessageDialog(this,
                "服务已关停，不能连入客户端请求",
                "消息",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 踢人功能
     * 根据输入的昵称找到对应客户端并断开连接
     */
    private void kickUser() {
        String name = kickNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要踢出的昵称", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClientHandler target = null;
        for (ClientHandler ch : clients) {
            if (ch.getNickname().equals(name)) {
                target = ch;
                break;
            }
        }

        if (target == null) {
            JOptionPane.showMessageDialog(this, "用户不存在", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 广播被踢消息
        broadcast("【" + getCurrentTime() + "," + name + "】：【因违规操作被踢出群聊室】", null);

        // 执行踢人并关闭连接
        target.kickOut();
        target.close();
        clients.remove(target);
        updateClientList();

        kickNameField.setText("");
        JOptionPane.showMessageDialog(this, "违规聊客已踢出", "消息", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 广播消息给所有在线客户端
     * @param message 要发送的消息
     * @param sender 发送者（本项目中未使用区分发送者）
     */
    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler ch : clients) {
            ch.sendMessage(message);
        }
        // 同时显示在服务器日志区域
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // 自动滚动到底部
    }

    /**
     * 移除断开连接的客户端
     * @param client 要移除的客户端
     * @param isKicked 是否被管理员踢出（true=不广播离开消息）
     */
    public void removeClient(ClientHandler client, boolean isKicked) {
        if (client == null || !clients.contains(client)) {
        return;
    }
        clients.remove(client);
        if (!isKicked && isRunning) {
            broadcast("【" + getCurrentTime() + "," + client.getNickname() + "】：【离开了群聊室】", null);
        }
        updateClientList();
    }
    //日志排查
//    public void removeClient(ClientHandler client, boolean isKicked) {
//        System.out.println("removeClient called: " + client.getNickname() + ", isKicked=" + isKicked + ", isRunning=" + isRunning);
//
//        clients.remove(client);
//        if (!isKicked && isRunning) {
//            System.out.println("广播离开消息: " + client.getNickname());
//            broadcast("【" + getCurrentTime() + "," + client.getNickname() + "】：【离开了群聊室】", null);
//        }
//        updateClientList();
//    }
    /**
     * 重载：默认不是被踢，正常离开
     */
    public void removeClient(ClientHandler client) {
        removeClient(client, false);
    }

    /**
     * 添加新客户端到在线列表
     */
    public void addClient(ClientHandler client) {
        clients.add(client);
        updateClientList();
    }

    // ========== Getter方法 ==========
    public List<ClientHandler> getClients() {
        return clients;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 迎宾线程：专门监听新客户端连接
     * 循环调用accept()，一旦有客户端接入就创建ClientHandler
     */
    class WelcomeThread extends Thread {
        public void run() {
            while (isRunning) {
                try {
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        serverSocket.setSoTimeout(500); // 设置超时，避免一直阻塞
                        Socket socket = serverSocket.accept();
                        if (isRunning) {
                            // 创建客户端处理器并启动
                            ClientHandler handler = new ClientHandler(socket, Server.this);
                            handler.start();
                        } else {
                            socket.close();
                        }
                    } else {
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    // 超时正常，继续循环
                    continue;
                } catch (SocketException e) {
                    // ServerSocket被关闭，正常退出
                    break;
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

    /**
     * 程序入口
     */
    public static void main(String[] args) {
        new Server();
    }
}