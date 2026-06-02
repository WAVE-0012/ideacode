import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class RedFlag extends JFrame {
    private GamePanel gamePanel;
    private JButton runButton, pauseButton, speedUpButton, speedDownButton;
    private JButton rotateLeftButton, rotateRightButton, fireButton;
    private JLabel timerLabel, scoreLabel;

    private javax.swing.Timer gameTimer;
    private int gameSeconds = 0;
    private int hitCount = 0;
    private int firedCount = 0;
    private int currentSpeed = 5;
    private int currentDirection = 0;
    private boolean isRunning = false;
    private boolean isPaused = false;

    public RedFlag() {
        setTitle("红旗拦截导弹-by241002302张颢议");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(true);

        getContentPane().setBackground(null);

        gamePanel = new GamePanel(this);
        gamePanel.setBackground(null);
        add(gamePanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setupKeyboardListeners();
        setupGameTimer();
        updateButtonStates();
        updateFireButtonText();

        setVisible(true);
    }

    private JPanel createBottomPanel() {
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        mainPanel.setBackground(null);

        timerLabel = new JLabel("0000 s");
        timerLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        timerLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(Color.WHITE);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setPreferredSize(new Dimension(80, 32));

        runButton = new JButton("运行");
        speedDownButton = new JButton("减速");
        rotateLeftButton = new JButton("逆时针旋转");
        fireButton = new JButton("拦截");
        rotateRightButton = new JButton("顺时针旋转");
        speedUpButton = new JButton("加速");
        pauseButton = new JButton("暂停");

        runButton.addActionListener(e -> startGame());
        pauseButton.addActionListener(e -> pauseGame());
        speedUpButton.addActionListener(e -> speedUp());
        speedDownButton.addActionListener(e -> speedDown());
        rotateLeftButton.addActionListener(e -> rotateLeft());
        rotateRightButton.addActionListener(e -> rotateRight());
        fireButton.addActionListener(e -> fireInterceptor());

        scoreLabel = new JLabel("0000 / 0000");
        scoreLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        scoreLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(Color.WHITE);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setPreferredSize(new Dimension(110, 32));

        mainPanel.add(timerLabel);
        mainPanel.add(runButton);
        mainPanel.add(speedDownButton);
        mainPanel.add(rotateLeftButton);
        mainPanel.add(fireButton);
        mainPanel.add(rotateRightButton);
        mainPanel.add(speedUpButton);
        mainPanel.add(pauseButton);
        mainPanel.add(scoreLabel);

        return mainPanel;
    }

    private void setupKeyboardListeners() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (isRunning && !isPaused) {
                            fireInterceptor();
                        }
                        return true;
                    }
                    return false;
                });
    }

    private void setupGameTimer() {
        gameTimer = new javax.swing.Timer(1000, e -> {
            if (isRunning && !isPaused) {
                gameSeconds++;
                timerLabel.setText(String.format("%04d s", gameSeconds));
            }
        });
        gameTimer.start();
    }

    private void startGame() {
        isRunning = true;
        isPaused = false;
        updateButtonStates();
        gamePanel.setRunning(true);
        gamePanel.setPaused(false);
    }

    private void pauseGame() {
        isPaused = true;
        isRunning = false;
        updateButtonStates();
        gamePanel.setRunning(false);
        gamePanel.setPaused(true);
    }

    private void speedUp() {
        if (currentSpeed < 8) {
            currentSpeed++;
            gamePanel.setCurrentSpeed(currentSpeed);
            updateButtonStates();
            updateFireButtonText();
        }
    }

    private void speedDown() {
        if (currentSpeed > 2) {
            currentSpeed--;
            gamePanel.setCurrentSpeed(currentSpeed);
            updateButtonStates();
            updateFireButtonText();
        }
    }

    private void rotateLeft() {
        if (currentDirection > -3) {
            currentDirection--;
            updateButtonStates();
            updateFireButtonText();
        }
    }

    private void rotateRight() {
        if (currentDirection < 3) {
            currentDirection++;
            updateButtonStates();
            updateFireButtonText();
        }
    }

    private void updateFireButtonText() {
        String dirDesc = "";
        if (currentDirection == -3) dirDesc = "-3舵";
        else if (currentDirection == -2) dirDesc = "-2舵";
        else if (currentDirection == -1) dirDesc = "-1舵";
        else if (currentDirection == 0) dirDesc = "0舵";
        else if (currentDirection == 1) dirDesc = "1舵";
        else if (currentDirection == 2) dirDesc = "2舵";
        else if (currentDirection == 3) dirDesc = "3舵";
        fireButton.setText("拦【" + currentSpeed + "速/" + dirDesc + "】截");
    }

    private void fireInterceptor() {
        if (isRunning && !isPaused) {
            double speed = 0.003 * currentSpeed;
            gamePanel.fireInterceptor(speed, currentDirection);
            firedCount++;
            scoreLabel.setText(String.format("%04d / %04d", hitCount, firedCount));
        }
    }

    private void updateButtonStates() {
        runButton.setEnabled(!isRunning || isPaused);
        pauseButton.setEnabled(isRunning && !isPaused);
        speedUpButton.setEnabled(currentSpeed < 8);
        speedDownButton.setEnabled(currentSpeed > 2);
        rotateLeftButton.setEnabled(currentDirection > -3);
        rotateRightButton.setEnabled(currentDirection < 3);
        fireButton.setEnabled(isRunning && !isPaused);
    }

    public void addHit() {
        hitCount++;
        scoreLabel.setText(String.format("%04d / %04d", hitCount, firedCount));
    }

    public int getCurrentSpeed() { return currentSpeed; }
    public int getCurrentDirection() { return currentDirection; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RedFlag());
    }
}