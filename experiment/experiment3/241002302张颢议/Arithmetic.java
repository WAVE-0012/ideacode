import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class Arithmetic extends JFrame {
    // 常量定义
    private static final int MAX_RANGE_VALUE = 10000;
    private static final int DEFAULT_MIN = 2;
    private static final int DEFAULT_MAX = 100;

    // 第1部分：操作数范围组件
    private JComboBox<Integer> minComboBox;
    private JComboBox<Integer> maxComboBox;

    // 第2部分：四则运算类型复选框
    private JCheckBox addCheckBox;
    private JCheckBox subCheckBox;
    private JCheckBox mulCheckBox;
    private JCheckBox divCheckBox;

    // 第3部分：算式生成部分
    private JButton generateButton;
    private JLabel expressionLabel;

    // 第4部分：运算结果输入
    private JTextField resultField;

    // 第5部分：判题和看答案按钮
    private JButton judgeButton;
    private JButton viewAnswerButton;

    // 当前生成的算式和答案
    private String currentExpression;
    private String currentAnswer;
    private int currentOperand1;
    private int currentOperand2;
    private char currentOperator;

    private Random random;

    public Arithmetic() {
        random = new Random();
        initComponents();
        setupLayout();
        addEventListeners();
        setDefaultValues();
    }

    private void initComponents() {
        // 初始化下拉框
        minComboBox = new JComboBox<>();
        maxComboBox = new JComboBox<>();

        // 填充下拉框数据 2~10000 和 10~10000
        for (int i = 2; i <= MAX_RANGE_VALUE; i++) {
            minComboBox.addItem(i);
        }
        for (int i = 10; i <= MAX_RANGE_VALUE; i++) {
            maxComboBox.addItem(i);
        }

        // 设置渲染器让下拉框显示更流畅
        minComboBox.setPrototypeDisplayValue(10000);
        maxComboBox.setPrototypeDisplayValue(10000);

        // 初始化复选框
        addCheckBox = new JCheckBox("加");
        subCheckBox = new JCheckBox("减");
        mulCheckBox = new JCheckBox("乘");
        divCheckBox = new JCheckBox("除");

        // 初始勾选加法和乘法
        addCheckBox.setSelected(true);
        mulCheckBox.setSelected(true);

        // 初始化按钮和标签
        generateButton = new JButton("生成新算式");
        expressionLabel = new JLabel("          ");
        expressionLabel.setFont(new Font("宋体", Font.BOLD, 18));
        expressionLabel.setBorder(BorderFactory.createEtchedBorder());
        expressionLabel.setOpaque(true);
        expressionLabel.setBackground(Color.WHITE);

        resultField = new JTextField(15);
        judgeButton = new JButton("判题");
        viewAnswerButton = new JButton("查看答案");
    }

    private void setupLayout() {
        setTitle("四则运算出题小软件--by241002302张颢议");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第1部分：操作数范围
        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rangePanel.setBorder(null);
        rangePanel.add(new JLabel("请选择一个操作数的最小值:"));
        rangePanel.add(minComboBox);
        rangePanel.add(new JLabel("请选择一个操作数的最大值:"));
        rangePanel.add(maxComboBox);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(rangePanel, gbc);

        // 第2部分：四则运算类型
        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        operationPanel.setBorder(null);
        operationPanel.add(new JLabel("请选择运算类型:"));
        operationPanel.add(addCheckBox);
        operationPanel.add(subCheckBox);
        operationPanel.add(mulCheckBox);
        operationPanel.add(divCheckBox);
        gbc.gridy = 1;
        mainPanel.add(operationPanel, gbc);

        // 第3部分：算式生成部分
        JPanel generatePanel = new JPanel(new BorderLayout(10, 5));
        generatePanel.setBorder(null);
        generatePanel.add(generateButton, BorderLayout.WEST);
        generatePanel.add(expressionLabel, BorderLayout.CENTER);
        gbc.gridy = 2;
        mainPanel.add(generatePanel, gbc);

        // 第4和第5部分：
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(null);

        // 左侧：运算结果
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        resultPanel.add(new JLabel("你的运算结果:"));
        resultField.setPreferredSize(new Dimension(150, 30));
        resultPanel.add(resultField);
        bottomPanel.add(resultPanel, BorderLayout.WEST);

        // 右侧：判题和看答案
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        judgeButton.setPreferredSize(new Dimension(100, 35));
        viewAnswerButton.setPreferredSize(new Dimension(100, 35));
        actionPanel.add(judgeButton);
        actionPanel.add(viewAnswerButton);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(bottomPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        setSize(600, 350);
        setLocationRelativeTo(null);
    }

    private void setDefaultValues() {
        // 设置默认最小值 2，最大值 100
        minComboBox.setSelectedItem(DEFAULT_MIN);
        maxComboBox.setSelectedItem(DEFAULT_MAX);
    }

    private void addEventListeners() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNewExpression();
            }
        });

        judgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                judgeAnswer();
            }
        });

        viewAnswerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAnswer();
            }
        });
    }

    private void generateNewExpression() {
        int minValue = (Integer) minComboBox.getSelectedItem();
        int maxValue = (Integer) maxComboBox.getSelectedItem();

        if (minValue >= maxValue) {
            JOptionPane.showMessageDialog(this,
                    "最小值必须小于最大值！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<Character> selectedOps = new ArrayList<>();
        if (addCheckBox.isSelected()) selectedOps.add('+');
        if (subCheckBox.isSelected()) selectedOps.add('-');
        if (mulCheckBox.isSelected()) selectedOps.add('*');
        if (divCheckBox.isSelected()) selectedOps.add('/');

        if (selectedOps.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请至少勾选一种运算类型！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        char operator = selectedOps.get(random.nextInt(selectedOps.size()));
        int operand1, operand2;

        switch (operator) {
            case '+':
                operand1 = getRandomInt(minValue, maxValue);
                operand2 = getRandomInt(minValue, maxValue);
                break;

            case '-':
                operand1 = getRandomInt(minValue, maxValue);
                operand2 = getRandomInt(minValue, maxValue);
                if (operand1 < operand2) {
                    int temp = operand1;
                    operand1 = operand2;
                    operand2 = temp;
                }
                break;

            case '*':
                operand1 = getRandomInt(minValue, maxValue);
                operand2 = getRandomInt(minValue, maxValue);
                break;

            case '/':
                // 修复的除法逻辑
                do {
                    operand2 = getRandomInt(minValue, maxValue);
                    if (operand2 == 0) operand2 = 1; // 避免除数为0

                    // 生成能整除的operand1
                    int minQuotient = Math.max(1, (int) Math.ceil((double) minValue / operand2));
                    int maxQuotient = Math.max(1, maxValue / operand2);

                    if (minQuotient > maxQuotient) {
                        // 如果找不到合适的商，重新生成除数
                        continue;
                    }

                    int quotient = getRandomInt(minQuotient, maxQuotient);
                    operand1 = operand2 * quotient;

                    // 检查结果是否在范围内
                    if (operand1 >= minValue && operand1 <= maxValue) {
                        break;
                    }
                } while (true);
                break;

            default:
                operand1 = getRandomInt(minValue, maxValue);
                operand2 = getRandomInt(minValue, maxValue);
        }

        currentOperand1 = operand1;
        currentOperand2 = operand2;
        currentOperator = operator;
        currentExpression = operand1 + " " + operator + " " + operand2;
        currentAnswer = calculateAnswer(operand1, operand2, operator);

        expressionLabel.setText(currentExpression);
        resultField.setText("");
    }

    private int getRandomInt(int min, int max) {
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return random.nextInt(max - min + 1) + min;
    }

    private String calculateAnswer(int operand1, int operand2, char operator) {
        int result;
        switch (operator) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                if (operand2 == 0) {
                    result = 0;
                } else {
                    result = operand1 / operand2;
                }
                break;
            default:
                result = 0;
        }
        return String.valueOf(result);
    }

    private void judgeAnswer() {
        if (currentExpression == null || currentExpression.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请先生成算式！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userInput = resultField.getText().trim();

        if (userInput.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请输入计算结果！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int userAnswer = Integer.parseInt(userInput);
            int correctAnswer = Integer.parseInt(currentAnswer);

            if (userAnswer == correctAnswer) {
                JOptionPane.showMessageDialog(this,
                        "回答正确！恭喜你！",
                        "恭喜",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "计算错误！" + "\n请重新计算或查看答案。",
                        "错误提示",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "您的计算结果不是一个整数，请重新输入！",
                    "输入错误",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showAnswer() {
        if (currentExpression == null || currentExpression.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请先生成算式！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String answerMessage = currentExpression.replace("?", "") + currentAnswer;
        JOptionPane.showMessageDialog(this,
                answerMessage,
                "正确答案",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Arithmetic frame = new Arithmetic();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}