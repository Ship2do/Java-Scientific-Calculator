package calculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Calculator extends JFrame {
    private JTextField display;
    private JPanel historyContentPanel;
    private JPanel historyPanel;
    private JButton historyToggleBtn;
    private JButton themeToggleBtn;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    
    private boolean isResultDisplayed = false;
    private boolean isDarkMode = false;
    // private ArrayList<String> history = new ArrayList<>();
    
    private static final int NORMAL_WIDTH = 500;
    private static final int EXPANDED_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private boolean historyVisible = false;
    
    private DecimalFormat df = new DecimalFormat("#.##########");
    
    public Calculator() {
        initializeUI();
        setupWindowListener();
    }
    
    private void initializeUI() {
        setTitle("科学计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // 主面板
        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        // 顶部面板：显示屏和历史记录按钮
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        // 显示屏
        display = new JTextField("0");
        display.setFont(new Font("微软雅黑", Font.BOLD, 28));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setForeground(Color.BLACK);
        display.setPreferredSize(new Dimension(0, 60));
        topPanel.add(display, BorderLayout.CENTER);
        
        // 顶部控制面板（主题切换 + 历史记录）
        JPanel topControlPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        
        // 主题切换按钮
        themeToggleBtn = new JButton("☀");
        themeToggleBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        themeToggleBtn.setToolTipText("切换深色/浅色模式");
        themeToggleBtn.addActionListener(e -> toggleTheme());
        updateButtonStyle(themeToggleBtn, new Color(240, 240, 240), Color.BLACK);
        topControlPanel.add(themeToggleBtn);
        
        // 历史记录切换按钮
        historyToggleBtn = new JButton("历史");
        historyToggleBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        historyToggleBtn.setToolTipText("显示/隐藏历史记录");
        historyToggleBtn.addActionListener(e -> toggleHistory());
        updateButtonStyle(historyToggleBtn, new Color(240, 240, 240), Color.BLACK);
        topControlPanel.add(historyToggleBtn);
        
        // 设置顶部控制面板的首选大小
        topControlPanel.setPreferredSize(new Dimension(140, 60));
        topPanel.add(topControlPanel, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // 中间面板：按钮区域和历史记录
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // 按钮面板
        buttonPanel = createButtonPanel();
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // 历史记录面板
        historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setPreferredSize(new Dimension(250, 0));
        historyPanel.setBackground(new Color(240, 240, 240));
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel historyLabel = new JLabel("历史记录");
        historyLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        historyLabel.setForeground(Color.BLACK);
        historyPanel.add(historyLabel, BorderLayout.NORTH);
        
        historyContentPanel = new JPanel();
        historyContentPanel.setLayout(new BoxLayout(historyContentPanel, BoxLayout.Y_AXIS));
        historyContentPanel.setBackground(new Color(240, 240, 240));
        
        JPanel historyWrapper = new JPanel(new BorderLayout());
        historyWrapper.setBackground(new Color(240, 240, 240));
        historyWrapper.add(historyContentPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(historyWrapper);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(240, 240, 240));
        // 总是显示垂直滚动条，避免出现时遮挡内容或引起布局跳动
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel historyControlPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        historyControlPanel.setBackground(new Color(240, 240, 240));
        
        JButton deleteBtn = new JButton("删除选中");
        deleteBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        deleteBtn.addActionListener(e -> deleteSelectedHistory());
        updateButtonStyle(deleteBtn, new Color(240, 240, 240), Color.BLACK);
        historyControlPanel.add(deleteBtn);
        
        JButton clearHistoryBtn = new JButton("清空历史");
        clearHistoryBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        clearHistoryBtn.addActionListener(e -> clearHistory());
        updateButtonStyle(clearHistoryBtn, new Color(240, 240, 240), Color.BLACK);
        historyControlPanel.add(clearHistoryBtn);
        
        historyPanel.add(historyControlPanel, BorderLayout.SOUTH);
        
        historyPanel.setVisible(false);
        centerPanel.add(historyPanel, BorderLayout.EAST);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        setSize(NORMAL_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(240, 240, 240));

        // 科学计算按钮面板
        JPanel scientificPanel = new JPanel(new GridLayout(3, 6, 5, 5));
        scientificPanel.setBackground(new Color(240, 240, 240));
        // 第一列是显示文本(HTML)，第二列是逻辑命令
        String[][] scientificButtons = {
            {"sin", "sin"}, {"cos", "cos"}, {"tan", "tan"}, {"ln", "ln"}, {"lg", "lg"}, {"√", "sqrt"},
            {"<html>sin<sup><small>-1</small></sup></html>", "asin"}, 
            {"<html>cos<sup><small>-1</small></sup></html>", "acos"}, 
            {"<html>tan<sup><small>-1</small></sup></html>", "atan"}, 
            {"<html>e<sup><small>x</small></sup></html>", "exp"}, 
            {"<html>10<sup><small>x</small></sup></html>", "pow10"}, 
            {"<html>x<sup><small>2</small></sup></html>", "sqr"},
            {"<html>x<sup><small>y</small></sup></html>", "^"},
            {"<html>log<sub><small>y</small></sub>x</html>", "log"}, // log y x
            {"π", "π"},
            {"e", "e"},
            {"(", "("}, 
            {")", ")"}
        };
        
        for (String[] btnData : scientificButtons) {
            JButton btn = createButton(btnData[0], btnData[1], new Color(240, 242, 245));
            // 特殊处理 log y x 按钮，防止换行
            if (btnData[1].equals("log")) {
                btn.setFont(new Font("微软雅黑", Font.BOLD, 15));
            }
            scientificPanel.add(btn);
        }
        
        panel.add(scientificPanel, BorderLayout.NORTH);
        
        // 标准计算器按钮面板
        JPanel standardPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        standardPanel.setBackground(new Color(240, 240, 240));
        String[] buttons = {
            "C", "CE", "←", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "±", "0", ".", "="
        };
        
        for (String text : buttons) {
            Color btnColor = Color.WHITE;
            if (text.matches("[0-9.]")) {
                btnColor = new Color(255, 255, 255);
            } else if (text.equals("=")) {
                btnColor = new Color(190, 215, 235);
            } else if (text.equals("C") || text.equals("CE") || text.equals("←")) {
                btnColor = new Color(235, 200, 200);
            } else if (text.matches("[÷×\\-+]")) {
                btnColor = new Color(230, 235, 240);
            }
            
            // 标准按钮的显示文本和命令相同
            JButton btn = createButton(text, text, btnColor);
            standardPanel.add(btn);
        }
        
        panel.add(standardPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createButton(String text, String command, Color bgColor) {
        JButton button = new JButton(text);
        button.setActionCommand(command);
        button.setFont(new Font("微软雅黑", Font.BOLD, 20));
        button.setBackground(bgColor);
        button.setForeground(new Color(50, 50, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.addActionListener(new ButtonClickListener());
        return button;
    }
    
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            processCommand(command);
        }
    }
    
    private void processCommand(String command) {
        if (isResultDisplayed) {
            if (command.matches("[0-9.]|sin|cos|tan|ln|lg|sqrt|asin|acos|atan|exp|pow10|\\(|π")) {
                display.setText("");
            }
            isResultDisplayed = false;
        }

        try {
            switch (command) {
                case "C":
                    display.setText("0");
                    break;
                case "CE":
                    if (isResultDisplayed) {
                        display.setText("0");
                        isResultDisplayed = false;
                    } else {
                        String text = display.getText();
                        if (!text.equals("0") && !text.isEmpty()) {
                            // 查找最后一个运算符或分隔符
                            String operators = "+-×÷^%(),";
                            int lastOpIndex = -1;
                            for (int i = text.length() - 1; i >= 0; i--) {
                                if (operators.indexOf(text.charAt(i)) != -1) {
                                    lastOpIndex = i;
                                    break;
                                }
                            }
                            
                            if (lastOpIndex == -1) {
                                // 没有运算符，说明当前只有一个数字，直接清零
                                display.setText("0");
                            } else if (text.length() > lastOpIndex + 1) {
                                // 保留到最后一个运算符（含），清除后面的数字
                                display.setText(text.substring(0, lastOpIndex + 1));
                            }
                            // 如果以运算符结尾，CE 不做任何操作（或者可以视需求决定是否删除运算符，通常 CE 只删数字）
                        }
                    }
                    break;
                case "←":
                    String text = display.getText();
                    if (text.length() > 0 && !text.equals("0")) {
                        text = text.substring(0, text.length() - 1);
                        if (text.isEmpty()) text = "0";
                        display.setText(text);
                    }
                    break;
                case "=":
                    calculateResult();
                    break;
                case "sin": case "cos": case "tan": 
                case "asin": case "acos": case "atan": 
                case "ln": case "lg":
                    insertText(command + "(");
                    break;
                case "sqrt":
                    insertText("√(");
                    break;
                case "log":
                    insertText("log_");
                    break;
                case "e":
                    insertText("e");
                    break;
                case "^":
                    insertText("^");
                    break;
                case "%":
                    insertText("%");
                    break;
                case "sqr":
                    insertText("^2");
                    break;
                case "exp":
                    insertText("e^");
                    break;
                case "pow10":
                    insertText("10^");
                    break;
                case "±":
                    // 简单的正负号处理：如果在数字前，加负号；如果在开头，加负号
                    // 这里简化处理，只在当前显示文本前加 -，或者去掉 -
                    String current = display.getText();
                    if (current.startsWith("-")) display.setText(current.substring(1));
                    else display.setText("-" + current);
                    break;
                default:
                    insertText(command);
                    break;
            }
        } catch (Exception ex) {
            display.setText("错误");
            isResultDisplayed = true;
        }
    }
    
    private void insertText(String text) {
        String current = display.getText();
        if (current.equals("0") && !text.startsWith(".")) {
            display.setText(text);
        } else {
            display.setText(current + text);
        }
    }
    
    private void calculateResult() {
        String expression = display.getText();
        try {
            double result = evaluateExpression(expression);
            
            // 检查结果是否为 NaN (非数字) 或 Infinity (无穷大)
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                display.setText("错误");
                addToHistory(expression + " = 错误");
            } else {
                String resultStr = df.format(result);
                display.setText(resultStr);
                addToHistory(expression + " = " + resultStr);
            }
            
            isResultDisplayed = true;
        } catch (Exception e) {
            display.setText("错误");
            addToHistory(expression + " = 错误");
            isResultDisplayed = true;
        }
    }

    // 简单的递归下降解析器
    private double evaluateExpression(String expression) throws Exception {
        // 预处理
        expression = expression.replace("×", "*").replace("÷", "/").replace("π", String.valueOf(Math.PI)).replace("√", "sqrt");
        
        final String expr = expression;
        
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else if (eat('%')) x %= parseFactor(); // modulus
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else if ((ch >= 'a' && ch <= 'z')) { // functions
                    while ((ch >= 'a' && ch <= 'z') || ch == '_') nextChar();
                    String func = expr.substring(startPos, this.pos);
                    if (func.equals("e")) {
                        x = Math.E;
                    } else {
                        x = parseFactor();
                        switch (func) {
                            case "sin": x = Math.sin(Math.toRadians(x)); break;
                            case "cos": x = Math.cos(Math.toRadians(x)); break;
                            case "tan": x = Math.tan(Math.toRadians(x)); break;
                            case "asin": x = Math.toDegrees(Math.asin(x)); break;
                            case "acos": x = Math.toDegrees(Math.acos(x)); break;
                            case "atan": x = Math.toDegrees(Math.atan(x)); break;
                            case "ln": x = Math.log(x); break;
                            case "lg": x = Math.log10(x); break;
                            case "sqrt": x = Math.sqrt(x); break;
                            case "log_":
                                double base = x;
                                if (eat('(')) {
                                    double val = parseExpression();
                                    eat(')');
                                    x = Math.log(val) / Math.log(base);
                                } else {
                                    throw new RuntimeException("Expected '(' after log base");
                                }
                                break;
                            default: throw new RuntimeException("Unknown function: " + func);
                        }
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
    
    private void addToHistory(String entry) {
        JPanel entryPanel = new JPanel(new BorderLayout(5, 0));
        Color panelBg = isDarkMode ? new Color(45, 45, 45) : new Color(240, 240, 240);
        Color borderColor = isDarkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY;
        Color fgColor = isDarkMode ? Color.WHITE : Color.BLACK;
        
        entryPanel.setBackground(panelBg);
        entryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            new EmptyBorder(5, 5, 5, 10) // 增加右侧内边距，防止滚动条遮挡
        ));
        
        JTextArea textArea = new JTextArea(entry);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setForeground(fgColor);
        
        JCheckBox checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
        checkBox.setBackground(panelBg);
        
        entryPanel.add(textArea, BorderLayout.CENTER);
        entryPanel.add(checkBox, BorderLayout.EAST);
        
        historyContentPanel.add(entryPanel, 0);
        historyContentPanel.revalidate();
        historyContentPanel.repaint();
    }
    
    private void deleteSelectedHistory() {
        for (int i = historyContentPanel.getComponentCount() - 1; i >= 0; i--) {
            Component comp = historyContentPanel.getComponent(i);
            if (comp instanceof JPanel) {
                JPanel entryPanel = (JPanel) comp;
                for (Component c : entryPanel.getComponents()) {
                    if (c instanceof JCheckBox) {
                        if (((JCheckBox) c).isSelected()) {
                            historyContentPanel.remove(i);
                        }
                        break;
                    }
                }
            }
        }
        historyContentPanel.revalidate();
        historyContentPanel.repaint();
    }
    
    private void clearHistory() {
        historyContentPanel.removeAll();
        historyContentPanel.revalidate();
        historyContentPanel.repaint();
    }
    
    private void toggleHistory() {
        historyVisible = !historyVisible;
        historyPanel.setVisible(historyVisible);
        
        if (historyVisible) {
            setSize(EXPANDED_WIDTH, WINDOW_HEIGHT);
        } else {
            setSize(NORMAL_WIDTH, WINDOW_HEIGHT);
        }
    }
    
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        updateTheme();
    }
    
    private void updateTheme() {
        Color bgColor = isDarkMode ? new Color(30, 30, 30) : Color.WHITE;
        Color fgColor = isDarkMode ? Color.WHITE : Color.BLACK;
        Color panelBg = isDarkMode ? new Color(45, 45, 45) : new Color(240, 240, 240);
        Color borderColor = isDarkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY;
        
        // 更新主界面背景
        getContentPane().setBackground(bgColor);
        mainPanel.setBackground(bgColor);
        
        // 更新显示屏
        display.setBackground(isDarkMode ? new Color(60, 60, 60) : Color.WHITE);
        display.setForeground(fgColor);
        
        // 更新顶部按钮
        themeToggleBtn.setText(isDarkMode ? "🌙" : "☀");
        updateButtonStyle(themeToggleBtn, isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240), fgColor);
        updateButtonStyle(historyToggleBtn, isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240), fgColor);
        
        // 更新历史记录面板及其子组件
        historyPanel.setBackground(panelBg);
        
        for (Component c : historyPanel.getComponents()) {
            if (c instanceof JLabel) {
                // 历史记录标题
                c.setForeground(fgColor);
            } else if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.getViewport().setBackground(panelBg);
                Component view = sp.getViewport().getView();
                if (view != null) view.setBackground(panelBg); // historyWrapper
            } else if (c instanceof JPanel) {
                // historyControlPanel (South)
                c.setBackground(panelBg);
                for (Component child : ((JPanel)c).getComponents()) {
                    if (child instanceof JButton) {
                        updateButtonStyle((JButton)child, isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240), fgColor);
                    }
                }
            }
        }
        
        historyContentPanel.setBackground(panelBg);
        
        // 更新历史记录条目
        for (Component comp : historyContentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel entryPanel = (JPanel) comp;
                entryPanel.setBackground(panelBg);
                entryPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
                    new EmptyBorder(5, 5, 5, 10)
                ));
                
                for (Component c : entryPanel.getComponents()) {
                    if (c instanceof JTextArea) {
                        c.setForeground(fgColor);
                    } else if (c instanceof JCheckBox) {
                        c.setBackground(panelBg);
                    }
                }
            }
        }
        
        // 更新所有计算器按钮
        updateCalculatorButtons();
        
        // 刷新界面
        repaint();
    }
    
    private void updateButtonStyle(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorderPainted(false); // 扁平化风格
        btn.setFocusPainted(false);
        btn.setOpaque(true);
    }
    
    private void updateCalculatorButtons() {
        // 遍历 buttonPanel 中的所有按钮并更新颜色
        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel subPanel = (JPanel) comp;
                subPanel.setBackground(isDarkMode ? new Color(30, 30, 30) : new Color(240, 240, 240));
                for (Component btnComp : subPanel.getComponents()) {
                    if (btnComp instanceof JButton) {
                        JButton btn = (JButton) btnComp;
                        String command = btn.getActionCommand();
                        
                        Color btnBg;
                        Color btnFg = isDarkMode ? new Color(230, 230, 230) : new Color(50, 50, 50);
                        
                        if (isDarkMode) {
                            // Dark Mode: 暗色调，不同功能区分
                            if (command.equals("=")) {
                                btnBg = new Color(0, 100, 160); // 深蓝色
                                btnFg = Color.WHITE;
                            } else if (command.matches("C|CE|←")) {
                                btnBg = new Color(160, 60, 60); // 深红色
                                btnFg = Color.WHITE;
                            } else if (command.matches("[0-9.]")) {
                                btnBg = new Color(60, 60, 60); // 深灰色 (数字)
                            } else if (command.matches("[÷×\\-+]")) {
                                btnBg = new Color(45, 45, 45); // 更深的灰色 (运算符)
                            } else {
                                // 科学计算 & 括号
                                btnBg = new Color(50, 50, 55); // 略带蓝调的深灰
                            }
                        } else {
                            // Light Mode: 素雅风格，同色系 (冷灰/蓝灰)
                            if (command.equals("=")) {
                                btnBg = new Color(190, 215, 235); // 淡蓝灰色
                            } else if (command.matches("C|CE|←")) {
                                btnBg = new Color(235, 200, 200); // 淡红灰色
                            } else if (command.matches("[0-9.]")) {
                                btnBg = new Color(255, 255, 255); // 纯白
                            } else if (command.matches("[÷×\\-+]")) {
                                btnBg = new Color(230, 235, 240); // 浅灰蓝
                            } else {
                                // 科学计算 & 括号
                                btnBg = new Color(240, 242, 245); // 极浅灰
                            }
                        }
                        
                        updateButtonStyle(btn, btnBg, btnFg);
                    }
                }
            }
        }
    }
    
    private void setupWindowListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                if (width >= EXPANDED_WIDTH - 50 && !historyVisible) {
                    historyVisible = true;
                    historyPanel.setVisible(true);
                } else if (width < EXPANDED_WIDTH - 50 && historyVisible) {
                    historyVisible = false;
                    historyPanel.setVisible(false);
                }
            }
        });
    }
    
    public static void main(String[] args) {          
            Calculator calculator = new Calculator();
            calculator.setVisible(true);        
    }
}
