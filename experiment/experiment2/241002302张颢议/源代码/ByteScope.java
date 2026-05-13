import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ByteScope extends JFrame {
    // 静态变量和动态变量
    private static final int ROWS_PER_PAGE = 30;
    private static final int BYTES_PER_ROW = 32;
    private static final int BYTES_PER_PAGE = ROWS_PER_PAGE * BYTES_PER_ROW;
    private byte[] fileBytes;
    private int currentPage = 0;
    private int totalPages = 0;
    private File selectedFile;
    private File resultFolder;

    // 图形界面组件
    private JTextArea hexDisplayArea;
    private JTextField filePathField;
    private JTextField folderPathField;
    private JTextField fileNameField;
    private JLabel summaryLabel;
    private JTextField pageInputField;
    private JLabel currentPageLabel;
    private JButton prevButton, nextButton, firstButton, lastButton;
    private JButton selectFileButton, parseButton, selectFolderButton, saveButton;
    private JButton goToPageButton;

    public ByteScope() {
        setTitle("文件字节显微镜--北林信息学院241002302张颢议");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // 文件选择行
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));

        selectFileButton = new JButton("选择待解析的文件");
        selectFileButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        filePanel.add(selectFileButton, BorderLayout.WEST);

        filePathField = new JTextField();
        filePathField.setEditable(false);
        filePathField.setBackground(Color.WHITE);
        filePathField.setForeground(new Color(102, 178, 255));
        filePathField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        filePathField.setText("请选择文件...");
        filePanel.add(filePathField, BorderLayout.CENTER);

        parseButton = new JButton("解析");
        parseButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        filePanel.add(parseButton, BorderLayout.EAST);

        topPanel.add(filePanel);

        // 摘要信息行
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryLabel = new JLabel("文件字节摘要：未加载文件");
        summaryLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        summaryPanel.add(summaryLabel);
        topPanel.add(summaryPanel);

        add(topPanel, BorderLayout.NORTH);

        // 中部显示区域
        JPanel centerPanel = new JPanel(new BorderLayout());
        hexDisplayArea = new JTextArea();
        hexDisplayArea.setEditable(false);
        hexDisplayArea.setFont(new Font("默认字体", Font.PLAIN, 13));
        hexDisplayArea.setBackground(Color.WHITE);
        hexDisplayArea.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(hexDisplayArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        hexDisplayArea.setText("请选择并解析文件...");

        add(centerPanel, BorderLayout.CENTER);

        // 底部面板
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        // 导航按钮行
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        firstButton = new JButton("首页");
        firstButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        firstButton.setPreferredSize(new Dimension(120, 28));

        prevButton = new JButton("<上一页");
        prevButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        prevButton.setPreferredSize(new Dimension(120, 28));

        pageInputField = new JTextField(10);
        pageInputField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        pageInputField.setPreferredSize(new Dimension(140, 28));
        pageInputField.setHorizontalAlignment(JTextField.CENTER);

        goToPageButton = new JButton("移至指定页");
        goToPageButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        goToPageButton.setPreferredSize(new Dimension(130, 28));

        nextButton = new JButton("下一页>");
        nextButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        nextButton.setPreferredSize(new Dimension(120, 28));

        lastButton = new JButton("尾页");
        lastButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lastButton.setPreferredSize(new Dimension(120, 28));

        currentPageLabel = new JLabel("当前页：0/0");
        currentPageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        navPanel.add(firstButton);
        navPanel.add(prevButton);
        navPanel.add(pageInputField);
        navPanel.add(goToPageButton);
        navPanel.add(nextButton);
        navPanel.add(lastButton);
        navPanel.add(currentPageLabel);

        bottomPanel.add(navPanel);

        // 选择文件夹行
        JPanel folderPanel = new JPanel(new BorderLayout(5, 0));
        selectFolderButton = new JButton("选择结果存放文件所在文件夹");
        selectFolderButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        folderPanel.add(selectFolderButton, BorderLayout.WEST);

        folderPathField = new JTextField();
        folderPathField.setEditable(false);
        folderPathField.setBackground(Color.WHITE);
        folderPathField.setForeground(new Color(102, 178, 255));
        folderPathField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        folderPathField.setText("请选择文件夹...");
        folderPanel.add(folderPathField, BorderLayout.CENTER);

        bottomPanel.add(folderPanel);

        // 存储操作行
        JPanel savePanel = new JPanel(new BorderLayout(5, 0));

        JPanel saveLeftPanel = new JPanel(new BorderLayout(5, 0));
        JLabel saveHintLabel = new JLabel("请输入要保存解析结果的不带后缀的文件名（后缀固定为txt.格式）：");
        saveHintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        saveLeftPanel.add(saveHintLabel, BorderLayout.WEST);

        fileNameField = new JTextField();
        fileNameField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        fileNameField.setBackground(Color.WHITE);
        saveLeftPanel.add(fileNameField, BorderLayout.CENTER);

        savePanel.add(saveLeftPanel, BorderLayout.CENTER);

        saveButton = new JButton("存储解析结果");
        saveButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        saveButton.setEnabled(false);
        savePanel.add(saveButton, BorderLayout.EAST);

        bottomPanel.add(savePanel);

        add(bottomPanel, BorderLayout.SOUTH);

        setAllNavigationEnabled(false);
        registerListeners();
    }

    private void registerListeners() {
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择要解析的文件");
                int result = fileChooser.showOpenDialog(ByteScope.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                    filePathField.setForeground(new Color(102, 178, 255));
                    fileBytes = null;
                    hexDisplayArea.setText("请点击\"解析\"按钮以查看内容...");
                    currentPage = 0;
                    totalPages = 0;
                    summaryLabel.setText("文件字节摘要：未解析");
                    setAllNavigationEnabled(false);
                    saveButton.setEnabled(false);
                }
            }
        });

        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseFile();
            }
        });

        firstButton.addActionListener(e -> goToFirstPage());
        prevButton.addActionListener(e -> goToPrevPage());
        nextButton.addActionListener(e -> goToNextPage());
        lastButton.addActionListener(e -> goToLastPage());

        goToPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToSpecificPage();
            }
        });

        selectFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser folderChooser = new JFileChooser();
                folderChooser.setDialogTitle("选择结果存放文件夹");
                folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = folderChooser.showOpenDialog(ByteScope.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    resultFolder = folderChooser.getSelectedFile();
                    folderPathField.setText(resultFolder.getAbsolutePath());
                    folderPathField.setForeground(new Color(102, 178, 255));
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveResults();
            }
        });
    }

    private void parseFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "请先选择文件！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!selectedFile.exists()) {
            JOptionPane.showMessageDialog(this, "错误：文件已不存在！\n" + selectedFile.getAbsolutePath(),
                    "文件不存在", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            fileBytes = readAllBytes(selectedFile);

            if (fileBytes.length == 0) {
                totalPages = 1;
            } else {
                int totalRows = (int) Math.ceil((double) fileBytes.length / BYTES_PER_ROW);
                totalPages = (int) Math.ceil((double) totalRows / ROWS_PER_PAGE);
            }

            currentPage = 0;
            updateSummary(selectedFile, fileBytes);

            if (fileBytes.length == 0) {
                hexDisplayArea.setText("");
                setAllNavigationEnabled(false);
            } else {
                displayCurrentPage();
                setAllNavigationEnabled(true);
                updateNavigationButtons();
            }

            saveButton.setEnabled(fileBytes.length > 0);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "读取文件时出错：" + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private byte[] readAllBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    private void updateSummary(File file, byte[] data) {
        long fileSize = file.length();

        String sizeStr;
        if (fileSize < 1024) {
            sizeStr = fileSize + "字节";
        } else if (fileSize < 1024 * 1024) {
            sizeStr = String.format("%.2fK", fileSize / 1024.0);
        } else {
            sizeStr = String.format("%.2fM", fileSize / (1024.0 * 1024.0));
        }

        summaryLabel.setText(String.format("文件字节摘要：共有%s字节，分为%d页", sizeStr, totalPages));
    }

    private int getRowNumberWidth() {
        if (fileBytes == null || fileBytes.length == 0) return 1;
        int totalRows = (int) Math.ceil((double) fileBytes.length / BYTES_PER_ROW);
        int maxRowNumber = totalRows - 1;
        return String.valueOf(maxRowNumber).length();
    }


    // 显示当前页
    private void displayCurrentPage() {
        if (fileBytes == null || fileBytes.length == 0) {
            hexDisplayArea.setText("");
            return;
        }

        int totalRows = (int) Math.ceil((double) fileBytes.length / BYTES_PER_ROW);
        int startRow = currentPage * ROWS_PER_PAGE; // 起始行号
        int endRow = Math.min(startRow + ROWS_PER_PAGE, totalRows); // 结束行号

        int rowNumberWidth = getRowNumberWidth();
        // 行号格式总宽度 = 数字位数 + 2（冒号+空格）
        int labelWidth = rowNumberWidth + 2;
        // 标题宽度至少等于labelWidth
        int titleWidth = Math.max(5, labelWidth);

        StringBuilder sb = new StringBuilder();

        // 第一行：列标题 - 每列固定3位宽度
        sb.append(String.format("%-" + titleWidth + "s", "行号"));
        for (int j = 0; j < BYTES_PER_ROW; j++) {
            sb.append(String.format("%3d", j));
        }
        sb.append("\n");

        // 分隔线
        sb.append(String.format("%-" + titleWidth + "s", "-----"));
        for (int j = 0; j < BYTES_PER_ROW; j++) {
            sb.append("---");
        }
        sb.append("\n");

        // 数据行：从startRow到endRow-1
        for (int row = startRow; row < endRow; row++) {
            // 十进制行号，补0，格式如 "00" 或 "000" 或 "0000"
            String rowLabel = String.format("%0" + rowNumberWidth + "d", row);
            // 使用titleWidth左对齐，保证和标题对齐
            sb.append(String.format("%-" + titleWidth + "s", rowLabel));

            int startByte = row * BYTES_PER_ROW;
            int endByte = Math.min(startByte + BYTES_PER_ROW, fileBytes.length);

            for (int j = 0; j < BYTES_PER_ROW; j++) {
                int index = startByte + j;
                if (index < endByte) {
                    byte b = fileBytes[index];
                    sb.append(String.format(" %02X ", b));
                } else {
                    sb.append("     ");
                }
            }
            sb.append("\n");
        }

        hexDisplayArea.setText(sb.toString());
        hexDisplayArea.setCaretPosition(0);
    }

    private void navigateToPage(int page) {
        if (fileBytes == null || fileBytes.length == 0) {
            return;
        }
        if (totalPages <= 0) {
            return;
        }
        if (page < 0 || page >= totalPages) {
            return;
        }

        currentPage = page;
        displayCurrentPage();
        updateNavigationButtons();
        updateCurrentPageLabel();
    }

    private void goToFirstPage() {
        if (fileBytes != null && fileBytes.length > 0 && totalPages > 0) {
            navigateToPage(0);
        }
    }

    private void goToPrevPage() {
        if (fileBytes != null && fileBytes.length > 0 && currentPage > 0) {
            navigateToPage(currentPage - 1);
        }
    }

    private void goToNextPage() {
        if (fileBytes != null && fileBytes.length > 0 && currentPage < totalPages - 1) {
            navigateToPage(currentPage + 1);
        }
    }

    private void goToLastPage() {
        if (fileBytes != null && fileBytes.length > 0 && totalPages > 0) {
            navigateToPage(totalPages - 1);
        }
    }

    private void goToSpecificPage() {
        if (fileBytes == null || fileBytes.length == 0) {
            JOptionPane.showMessageDialog(this, "没有可浏览的页面！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (totalPages <= 0) {
            JOptionPane.showMessageDialog(this, "没有可浏览的页面！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = pageInputField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入页码！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int pageNumber = Integer.parseInt(input);
            if (pageNumber <= 0) {
                JOptionPane.showMessageDialog(this, "请输入大于0的页码！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pageNumber > totalPages) {
                JOptionPane.showMessageDialog(this, "页码超出总页数！总页数：" + totalPages, "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            navigateToPage(pageNumber - 1);
            pageInputField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的整数页码！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateNavigationButtons() {
        if (fileBytes == null || fileBytes.length == 0 || totalPages <= 0) {
            setAllNavigationEnabled(false);
            return;
        }

        firstButton.setEnabled(currentPage > 0);
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
        lastButton.setEnabled(currentPage < totalPages - 1);

        goToPageButton.setEnabled(true);
        pageInputField.setEnabled(true);

        updateCurrentPageLabel();
    }

    private void updateCurrentPageLabel() {
        if (fileBytes == null || fileBytes.length == 0 || totalPages <= 0) {
            currentPageLabel.setText("当前页：0/0");
        } else {
            currentPageLabel.setText(String.format("当前页：%d/%d", currentPage + 1, totalPages));
        }
    }

    private void setAllNavigationEnabled(boolean enabled) {
        firstButton.setEnabled(enabled);
        prevButton.setEnabled(enabled);
        nextButton.setEnabled(enabled);
        lastButton.setEnabled(enabled);
        goToPageButton.setEnabled(enabled);
        pageInputField.setEnabled(enabled);

        if (!enabled) {
            pageInputField.setText("");
            currentPageLabel.setText("当前页：0/0");
        }
    }

    // 存储解析结果
    // 存储解析结果
    private void saveResults() {
        if (fileBytes == null || fileBytes.length == 0) {
            JOptionPane.showMessageDialog(this, "没有解析结果可保存！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (resultFolder == null) {
            JOptionPane.showMessageDialog(this, "请先选择结果存放文件夹！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fileName = fileNameField.getText().trim();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入文件名！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fileName = fileName + ".txt";
        File outputFile = new File(resultFolder, fileName);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), "UTF-8"))) {

            writer.println("文件字节显微镜解析结果");
            writer.println("解析文件: " + selectedFile.getAbsolutePath());
            writer.println("文件大小: " + selectedFile.length() + " 字节");
            writer.println("总页数: " + totalPages);
            writer.println("========================================");

            int totalRows = (int) Math.ceil((double) fileBytes.length / BYTES_PER_ROW);
            int rowNumberWidth = getRowNumberWidth();
            int labelWidth = rowNumberWidth + 2;
            int titleWidth = Math.max(5, labelWidth);

            for (int page = 0; page < totalPages; page++) {
                writer.println();
                writer.println("--- 第 " + (page + 1) + " 页 / 共 " + totalPages + " 页 ---");

                // 列标题 - 每列固定3位宽度
                writer.printf("%-" + titleWidth + "s", "行号");
                for (int j = 0; j < BYTES_PER_ROW; j++) {
                    writer.printf("%3d", j);
                }
                writer.println();

                // 分隔线
                writer.printf("%-" + titleWidth + "s", "-----");
                for (int j = 0; j < BYTES_PER_ROW; j++) {
                    writer.print("---");
                }
                writer.println();

                int startRow = page * ROWS_PER_PAGE;
                int endRow = Math.min(startRow + ROWS_PER_PAGE, totalRows);

                for (int row = startRow; row < endRow; row++) {
                    String rowLabel = String.format("%0" + rowNumberWidth + "d", row);
                    writer.printf("%-" + titleWidth + "s", rowLabel);

                    int startByte = row * BYTES_PER_ROW;
                    int endByte = Math.min(startByte + BYTES_PER_ROW, fileBytes.length);

                    for (int j = 0; j < BYTES_PER_ROW; j++) {
                        int index = startByte + j;
                        if (index < endByte) {
                            byte b = fileBytes[index];
                            writer.printf(" %02X ", b);
                        } else {
                            writer.print("     ");
                        }
                    }
                    writer.println();
                }
            }

            JOptionPane.showMessageDialog(this, "解析结果已保存到：\n" + outputFile.getAbsolutePath(),
                    "保存成功", JOptionPane.INFORMATION_MESSAGE);

            fileNameField.setText("");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "保存文件时出错：" + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ByteScope byteScope = new ByteScope();
            byteScope.setVisible(true);
        });
    }
}