import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    private JTextField ipField;
    private JTextField portField;
    private JTextField nicknameField;
    private JTextArea chatArea;
    private JTextArea inputArea;
    private JButton connectBtn;
    private JButton sendBtn;
    private JButton exitBtn;

    private Socket socket;
    private PrintWriter writer;
    private ReceiveThread receiveThread;
    private SendThread sendThread;
    private String nickname;
    private boolean isInChatRoom = false;
    private boolean isManualExit = false;

    public Client() {
        setTitle("全民大讨论聊天室客户端--241002302张颢议");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 5));

        JPanel topContainer = new JPanel(new GridLayout(2, 1, 0, 5));
        topContainer.setBorder(BorderFactory.createEmptyBorder(8, 10, 5, 10));

        JPanel inputPanel = new JPanel(new GridLayout(1, 6, 8, 0));

        JLabel ipLabel = new JLabel("服务器IP:", SwingConstants.CENTER);
        ipField = new JTextField("localhost");
        ipField.setHorizontalAlignment(JTextField.CENTER);

        JLabel portLabel = new JLabel("服务器端口:", SwingConstants.CENTER);
        portField = new JTextField("3083");
        portField.setHorizontalAlignment(JTextField.CENTER);

        JLabel nicknameLabel = new JLabel("昵称:", SwingConstants.CENTER);
        nicknameField = new JTextField();
        nicknameField.setHorizontalAlignment(JTextField.CENTER);

        inputPanel.add(ipLabel);
        inputPanel.add(ipField);
        inputPanel.add(portLabel);
        inputPanel.add(portField);
        inputPanel.add(nicknameLabel);
        inputPanel.add(nicknameField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        connectBtn = new JButton("进入聊天室");
        exitBtn = new JButton("退出聊天室");
        exitBtn.setEnabled(false);

        buttonPanel.add(connectBtn);
        buttonPanel.add(exitBtn);

        topContainer.add(inputPanel);
        topContainer.add(buttonPanel);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("宋体", Font.PLAIN, 12));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        inputArea = new JTextArea(3, 30);
        inputArea.setFont(new Font("宋体", Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);

        sendBtn = new JButton("发送");
        sendBtn.setEnabled(false);
        sendBtn.setPreferredSize(new Dimension(70, 50));

        bottomPanel.add(inputScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);

        add(topContainer, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        connectBtn.addActionListener(e -> connectToServer());
        exitBtn.addActionListener(e -> disconnect());
        sendBtn.addActionListener(e -> {
            if (!isInChatRoom || sendThread == null) {
                JOptionPane.showMessageDialog(this, "未连接到服务器，无法发送消息", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String msg = inputArea.getText().trim();
            if (!msg.isEmpty()) {
                sendThread.sendMessage(msg);
                inputArea.setText("");
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isInChatRoom) {
                    disconnect();
                } else {
                    System.exit(0);
                }
            }
        });

        setVisible(true);
    }

    private String getCurrentTime() {
        return new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
    }

    private void connectToServer() {
        String ip = ipField.getText().trim();
        String portStr = portField.getText().trim();
        nickname = nicknameField.getText().trim();

        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "服务器IP不能为空");
            return;
        }
        if (portStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "服务器端口不能为空");
            return;
        }
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "昵称不能为空");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
            if (port < 1024 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "端口必须是1024~65535之间的整数");
            return;
        }

        try {
            socket = new Socket(ip, port);
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(nickname);

            BufferedReader tempReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String reply = tempReader.readLine();

            if (reply == null || reply.startsWith("ERROR")) {
                String errorMsg = (reply != null && reply.startsWith("ERROR")) ? reply.substring(6) : "连接失败";
                JOptionPane.showMessageDialog(this, errorMsg, "消息", JOptionPane.INFORMATION_MESSAGE);
                socket.close();
                return;
            }

            // 成功进入弹窗
            JOptionPane.showMessageDialog(this, "您已经成功进入群聊室，开始聊天吧", "消息", JOptionPane.INFORMATION_MESSAGE);

            receiveThread = new ReceiveThread(socket, chatArea, this);
            receiveThread.start();

            sendThread = new SendThread(writer,this);
            sendThread.start();

            // 立即更新UI
            connectBtn.setEnabled(false);
            exitBtn.setEnabled(true);
            sendBtn.setEnabled(true);
            ipField.setEnabled(false);
            portField.setEnabled(false);
            nicknameField.setEnabled(false);

            isInChatRoom = true;
            isManualExit = false;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "连接失败：" + e.getMessage());
        }
    }

    // 用户主动退出
    public void disconnect() {
        if (!isInChatRoom) {
            return;
        }

        isManualExit = true;

        // 先发送退出消息给服务器
        if (writer != null) {
            writer.println("exit");
            writer.flush();
        }

        // 立即恢复UI（不等弹窗）
        connectBtn.setEnabled(true);
        exitBtn.setEnabled(false);
        sendBtn.setEnabled(false);
        ipField.setEnabled(true);
        portField.setEnabled(true);
        nicknameField.setEnabled(true);
        nicknameField.setText("");

        // 显示离开消息（本地）
        chatArea.append("【" + getCurrentTime() + "," + nickname + "】：【离开了群聊室】\n");

        // 弹窗（不影响消息显示）
        JOptionPane.showMessageDialog(this, "您已经离开群聊室", "消息", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(this, "通信中断，请重新连接", "消息", JOptionPane.INFORMATION_MESSAGE);

        // 关闭资源
        if (sendThread != null) {
            sendThread.stopThread();
            sendThread = null;
        }
        if (receiveThread != null) {
            receiveThread.stopThread();
            receiveThread = null;
        }

        try {
            if (writer != null) {
                writer.close();
                writer = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        isInChatRoom = false;
    }

    // 被动断开
    public void disconnectPassive() {
        if (!isInChatRoom) {
            return;
        }

        // 立即标记为不在聊天室
        isInChatRoom = false;

        // 停止线程
        if (sendThread != null) {
            sendThread.stopThread();
            sendThread = null;
        }
        if (receiveThread != null) {
            receiveThread.stopThread();
            receiveThread = null;
        }

        // 关闭网络资源
        try {
            if (writer != null) {
                writer.close();
                writer = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 恢复UI
        SwingUtilities.invokeLater(() -> {
            connectBtn.setEnabled(true);
            exitBtn.setEnabled(false);
            sendBtn.setEnabled(false);
            ipField.setEnabled(true);
            portField.setEnabled(true);
            nicknameField.setEnabled(true);
            nicknameField.setText("");
        });
    }

    public boolean isManualExit() {
        return isManualExit;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}