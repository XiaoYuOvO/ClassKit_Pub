package net.xiaoyu233.classkit.gui;

import net.xiaoyu233.classkit.api.Class;
import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.random.ClassRandomizer;
import net.xiaoyu233.classkit.random.Randomizer;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class RandomWindowMain extends JFrame {
    private static final Font BIG_FONT = new Font(null, Font.PLAIN, 50);
    private static final Font MIDDLE = new Font(null, Font.PLAIN, 30);
    private static final int HEIGHT = 480;
    private static final int WIDTH = 600;
    private final JLabel who = new JLabel("<请开始摇号>");
    private final ClassRandomizer classRandomizer;
    private final List<Student> excludes = new ArrayList<>();
    private Randomizer randThread;
    private final JTextArea logArea = new JTextArea();

    public RandomWindowMain(Class clazz) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        this.classRandomizer = new ClassRandomizer(SecureRandom::new, new ClassManager(clazz));
        this.setBounds(1920 / 2 - WIDTH / 2, 1080 / 2 - HEIGHT / 2, WIDTH, HEIGHT);
        this.setResizable(false);
        this.setLayout(null);
        JTabbedPane tabbedPane  = new JTabbedPane();
        tabbedPane.add(this.createSingleRandPanel());
        tabbedPane.add(this.createMultiRandPanel());

        this.setJMenuBar(this.createMenu());
        this.setContentPane(tabbedPane);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        JFrame frame = new JFrame();
        JScrollPane comp = new JScrollPane();
        comp.setViewportView(this.logArea);
        frame.add(comp);
        EventQueue.invokeLater(() -> frame.setVisible(true));
    }

    private JMenuBar createMenu(){
        JMenuBar jMenuBar = new JMenuBar();
        JMenu c = new JMenu("选项");
        JMenuItem menuItem = new JMenuItem("设置");
        menuItem.addActionListener((e)-> {

        });
        c.add(menuItem);
        jMenuBar.add(c);
        return jMenuBar;
    }

    private JPanel createMultiRandPanel(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);
        jPanel.setName("多抽更省力");
        JFormattedTextField count = new JFormattedTextField();
        NumberFormatter defaultFormat = new NumberFormatter();
        defaultFormat.setAllowsInvalid(false);
        NumberFormat integerInstance = NumberFormat.getIntegerInstance();
        integerInstance.setGroupingUsed(false);
        defaultFormat.setFormat(integerInstance);
        count.setFormatterFactory(new DefaultFormatterFactory(defaultFormat));
        count.setBounds(0,0,200,20);
        JComboBox<Gender> gender = new JComboBox<>();
        for (Gender value : Gender.values()) {
            gender.addItem(value);
        }
        gender.setBounds(210,0,160,20);
        gender.setSelectedItem(Gender.None);
        JScrollPane scrollPane = new JScrollPane();
        JTextArea resultText = new JTextArea();
        scrollPane.setViewportView(resultText);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setHorizontalScrollBar(new JScrollBar());
        scrollPane.setVerticalScrollBar(new JScrollBar());
        scrollPane.setEnabled(true);
        scrollPane.setBounds(60,60,400,290);
        resultText.setBounds(0,0,600,600);
        resultText.setEditable(false);
        resultText.setFont(MIDDLE);
        scrollPane.updateUI();
        JButton next = new JButton("摇号");
        next.setBounds(380, 0, 120, 60);
        next.addActionListener((e)->{
            List<Student> result = new ArrayList<>();
            Object value = count.getValue();
            if (value != null){
                long value1 = (Long) value;
                Gender targetGender = gender.getItemAt(gender.getSelectedIndex());
                if (value1 <= this.classRandomizer.getClassManager().getStudentCount(targetGender)){
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < value1; i++) {
                        Student e1 = this.classRandomizer.randomGetStudent(this.excludes, targetGender);
                        if (e1 != null){
                            sb.append(e1.getName()).append("\n");
                            result.add(e1);
                            this.excludes.add(e1);
                        }
                    }
                    resultText.setText(sb.toString());
                    resultText.updateUI();
                }else {
                    JOptionPane.showMessageDialog(jPanel, "人数过多", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }else {
                JOptionPane.showMessageDialog(jPanel, "请输入数量", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        jPanel.add(gender);
        jPanel.add(count);
        jPanel.add(next);
        jPanel.add(scrollPane);
        return jPanel;
    }

    private JPanel createSingleRandPanel(){
        JPanel jPanel = new JPanel();
        jPanel.setName("单抽出奇迹");
        jPanel.setLayout(null);
        JCheckBox fancy_mode = new JCheckBox("FANCY MODE");
        JComboBox<Gender> gender = new JComboBox<>();
        for (Gender value : Gender.values()) {
            gender.addItem(value);
        }
        fancy_mode.setBounds(200,200,160,20);
        gender.setBounds(360,200,160,20);
        gender.setSelectedItem(Gender.None);
        JButton next = new JButton("摇号");
        next.setBounds(200, 240, 200, 100);
        next.addActionListener((e) -> {
            this.randThread = new Randomizer(2,this.classRandomizer,
                    text -> {
                if (text != null){
                        this.who.setForeground(Color.GRAY);
                        this.who.setText(text.getName());
                        this.who.repaint();
                        next.setEnabled(false);
                    }else {
                        this.who.setText("抽完了");
                    }
                    }, text -> {
                if (text != null){
                    this.who.setForeground(Color.BLACK);
                    this.who.setText(text.getName());
                    this.who.repaint();
                    next.setEnabled(true);
//                    this.excludes.add(text);
                    logArea.append(text.getName() + "\n");
                }else {
                    this.who.setText("抽完了");
                }
                    }
            );
            if (fancy_mode.isSelected()) {
                randThread.setRound((rand) -> rand.nextInt(7) + 6);
            } else {
                randThread.setRound((rand) -> 1);
            }
            randThread.setTargetGender(gender.getItemAt(gender.getSelectedIndex()));
            randThread.setExclude(this.excludes);
            randThread.start();
        });
        who.setBounds(0, 0, WIDTH, HEIGHT / 2);
        who.setAlignmentX(150);
        who.setFont(BIG_FONT);
        who.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel.add(next);
        jPanel.add(who);
        jPanel.add(fancy_mode);
        jPanel.add(gender);
        return jPanel;
    }
}
