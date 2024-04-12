package net.xiaoyu233.classkit.gui;

import net.xiaoyu233.classkit.api.Class;
import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.ClassSize;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.config.PosManagerConfig;
import net.xiaoyu233.classkit.gui.dialog.AboutDialog;
import net.xiaoyu233.classkit.gui.table.ChangeHistoryTableModel;
import net.xiaoyu233.classkit.gui.table.ClassDisplay;
import net.xiaoyu233.classkit.io.ClassCodec;
import net.xiaoyu233.classkit.random.ClassRandomizer;
import net.xiaoyu233.classkit.random.gender.RandomGenderProvider;
import net.xiaoyu233.classkit.util.Fonts;
import net.xiaoyu233.classkit.util.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.security.SecureRandom;

public class PosManagerWindow extends JFrame {
    private static final int DEFAULT_WIDTH = 600,DEFAULT_HEIGHT = 400;
    private static final Border DEBUG_BORDER = BorderFactory.createLineBorder(Color.RED,10);
    private final PosManagerConfig config;
    private final JComponent classTable;
    private final JTable historyTable;
    private ClassManager manager;
    private boolean onSingleChangeMode;
    private JMenuItem changeButton;
    private Student changingLeft = Student.EMPTY,changingRight = Student.EMPTY;
    private static final File configFile = new File("./pos_mng_cfg.json");
    private static final File workdir = new File(System.getProperty("user.dir"));

    public PosManagerWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        this.config = new PosManagerConfig(configFile,this);
        this.manager = new ClassManager(new Class(ClassSize.Builder.create()
                                                          .addGroup(8)
                                                          .addGroup(8).
                                                          addGroup(8).
                                                          addGroup(8).build()));
        this.setLayout(new BorderLayout());
        this.add(this.createLecture(),BorderLayout.NORTH);
        classTable = this.createClassTable(this.manager);
        this.add(classTable, BorderLayout.CENTER);
        this.historyTable = new JTable(new ChangeHistoryTableModel(this.manager.getHistory()));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setJMenuBar(this.createControlMenu());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.classTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_X){
                    PosManagerWindow.this.onSwitchSingleChangeMode();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_X){
                    PosManagerWindow.this.onSwitchSingleChangeMode();
                }
            }
        });
    }

    private void saveConfig(){
        this.config.writeConfig();
    }

    private void readConfig(){
        this.config.readConfig();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (this.onSingleChangeMode){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font font = g.getFont();
            String toDraw = "将 " + this.changingLeft.getName() + " 调换至 " + this.changingRight.getName();
            g.setFont(Fonts.BIG_BIG_FONT);
            FontMetrics fontMetrics = g.getFontMetrics();
            Rectangle2D stringBounds = fontMetrics.getStringBounds(toDraw, g);
            g.drawString(toDraw, (int) ((this.getWidth() - stringBounds.getWidth()) / 2), (int) (this.getHeight() * 0.75));
            toDraw = "提示:对座位表按下鼠标左右键来选择目标";
            g.setFont(Fonts.MIDDLE_FONT);
            fontMetrics = g.getFontMetrics();
            g.drawString(toDraw, (this.getWidth() - fontMetrics.stringWidth(toDraw)) / 2, (int) (this.getHeight() * 0.75 + stringBounds.getHeight() * 2));
            g.setFont(font);
        }
    }

    private JMenuBar createControlMenu(){
        JMenuBar menuBar = new JMenuBar();
        //Files
        {
            JMenu fileMenu = new JMenu("文件");
            //Import
            {
                JMenuItem importJson = new JMenuItem("导入JSON配置文件");
                importJson.addActionListener(e -> {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setFileFilter(new FileNameExtensionFilter("JSON配置文件","json"));
                    jFileChooser.setFileSystemView(jFileChooser.getFileSystemView());
                    jFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                    jFileChooser.setCurrentDirectory(workdir);
                    jFileChooser.setAcceptAllFileFilterUsed(false);
                    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    jFileChooser.setMultiSelectionEnabled(false);
                    jFileChooser.setDialogTitle("导入座位表");
                    jFileChooser.showOpenDialog(this);
                    try {
                        File filePath = jFileChooser.getSelectedFile();
                        if (filePath != null){
                            Class clazz = ClassCodec.ClassDeserializer.readFromFile(filePath);
                            manager.manageNewClass(clazz);
                            ((ChangeHistoryTableModel) this.historyTable.getModel()).setHistories(manager.getHistory());
                        }
                        this.repaint();
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(this,"导入失败! \n" + exception.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
                        exception.printStackTrace();
                    }
                });
                fileMenu.add(importJson);
            }
            //Export
            {
                JMenu export = new JMenu("导出");
                //CSV Table
                {
                    JMenuItem csv = createCsvExportMenu();
                    export.add(csv);
                }
                //JSON Config Files
                {
                    JMenuItem json = createJsonExportMenu();
                    export.add(json);
                }
                fileMenu.add(export);
            }
            //Settings
            {
                fileMenu.add(new JSeparator());
                JMenuItem settings = new JMenuItem("设置");
                settings.addActionListener(e -> {
                    this.config.showConfigDialog();
                });
                fileMenu.add(settings);
            }
            menuBar.add(fileMenu);
        }
        //Operations
        {
            JMenu operations = new JMenu("操作");
            //Single Exchange
            {
                changeButton = new JMenuItem("单独调换");
                changeButton.addActionListener(e -> this.onSwitchSingleChangeMode());

                operations.add(changeButton);
            }
            //RandomNew
            {
                JMenuItem randomNewClass = createRandomNewMenu();
                operations.add(randomNewClass);
            }
            menuBar.add(operations);
        }
        //Help
        {
            JMenu help = new JMenu("帮助");
            //About
            {
                JMenuItem about = new JMenuItem("关于");
                about.addActionListener(e -> {
                    AboutDialog.showForPosManager(this);
                });
                help.add(about);
            }
            menuBar.add(help);
        }
        return menuBar;
    }

    private JMenuItem createRandomNewMenu() {
        JMenuItem randomNewClass = new JMenuItem("随机调换");
        randomNewClass.addActionListener(e -> {
            ClassRandomizer classRandomizer = new ClassRandomizer(SecureRandom::new, manager);
//                    if (config.keepGender()){
                classRandomizer.setGenderMapProvider(RandomGenderProvider.INSTANCE);
//                    }
            this.manager = classRandomizer.randomNewClass();
            ((ChangeHistoryTableModel) this.historyTable.getModel()).setHistories(manager.getHistory());
            this.repaint();
        });
        return randomNewClass;
    }

    private JMenuItem createCsvExportMenu() {
        JMenuItem csv = new JMenuItem("CSV表格");
        csv.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileNameExtensionFilter("CSV表格","csv"));
            jFileChooser.setFileSystemView(jFileChooser.getFileSystemView());
            jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            jFileChooser.setAcceptAllFileFilterUsed(false);
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.setMultiSelectionEnabled(false);
            jFileChooser.setCurrentDirectory(workdir);
            jFileChooser.setDialogTitle("导出座位表[CSV表格]");
            jFileChooser.showOpenDialog(this);
            try {
                File selectedFile = jFileChooser.getSelectedFile();
                if (selectedFile != null){
                    String filePath = selectedFile.toString();
                    manager.writeToCSV(new File(filePath.endsWith(".csv") ? filePath : filePath + ".csv"));
                    JOptionPane.showMessageDialog(this,"导出成功");
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this,"导出失败! \n" + exception.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
                exception.printStackTrace();
            }
        });
        return csv;
    }

    private JMenuItem createJsonExportMenu() {
        JMenuItem json = new JMenuItem("JSON配置文件");
        json.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileNameExtensionFilter("JSON配置文件","json"));
            jFileChooser.setFileSystemView(jFileChooser.getFileSystemView());
            jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            jFileChooser.setAcceptAllFileFilterUsed(false);
            jFileChooser.setCurrentDirectory(workdir);
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.setMultiSelectionEnabled(false);
            jFileChooser.setDialogTitle("导出座位表[JSON配置文件]");
            jFileChooser.showOpenDialog(this);
            try {
                File selectedFile = jFileChooser.getSelectedFile();
                if (selectedFile != null){
                    String filePath = selectedFile.toString();
                    manager.writeToFile(new File(filePath.endsWith(".json") ? filePath : filePath + ".json"));
                    JOptionPane.showMessageDialog(this,"导出成功");
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this,"导出失败! \n" + exception.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
                exception.printStackTrace();
            }
        });
        return json;
    }

    private void onSwitchSingleChangeMode(){
        if (this.onSingleChangeMode){
            changeButton.setText("单独调换");
            this.onSingleChangeMode = false;
            this.changingLeft = Student.EMPTY;
            this.changingRight = Student.EMPTY;
            this.classTable.setBorder(BorderFactory.createEmptyBorder());
        }else {
            changeButton.setText("取消单独调换");
            this.onSingleChangeMode = true;
            this.classTable.setBorder(DEBUG_BORDER);
        }
        this.repaint();
    }

    private JComponent createClassTable(ClassManager manager){
//        JScrollPane scrollPane = new JScrollPane();
        ClassDisplay display = new ClassDisplay(manager);
        display.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                try {
                    Utils.fitTableColumns(display);
                    PosManagerWindow.this.doLayout();
                    PosManagerWindow.this.repaint();
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        display.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onSingleChangeMode){
                    JTable source = (JTable) e.getSource();
                    Student targetStudent = (Student) source.getModel().getValueAt(source.rowAtPoint(e.getPoint()), source.columnAtPoint(e.getPoint()));
                    if (e.getButton() == 1) {
                        if (PosManagerWindow.this.changingLeft.isEmpty()){
                            PosManagerWindow.this.changingLeft = targetStudent;
                        }else {
                            PosManagerWindow.this.changingRight = targetStudent;
                        }
                    }else if (e.getButton() == 3) {
                        PosManagerWindow.this.changingRight = targetStudent;
                    }
                    PosManagerWindow.this.repaint();
                    if (changingLeft != Student.EMPTY && changingRight != Student.EMPTY){
                        if (JOptionPane.showConfirmDialog(PosManagerWindow.this, "确认调换", "调换确认", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                            PosManagerWindow.this.manager.exchangeStudent(changingLeft,changingRight,true);
                            PosManagerWindow.this.historyTable.updateUI();
                            PosManagerWindow.this.historyTable.repaint();
                        }
                        PosManagerWindow.this.repaint();
                        PosManagerWindow.this.onSwitchSingleChangeMode();
                    }
                }
            }
        });
        return display;
    }

    private JComponent createLecture(){
        JPanel lectureHolder = new JPanel();
        lectureHolder.setLayout(new FlowLayout());
        JLabel lecture = new JLabel("讲台");
        lecture.setBorder(BorderFactory.createRaisedBevelBorder());
        lecture.setFont(Fonts.BIG_BIG_FONT);
        lecture.setHorizontalAlignment(SwingConstants.LEADING);
        lecture.setVerticalAlignment(SwingConstants.CENTER);
        lectureHolder.add(lecture,BorderLayout.CENTER);
        return lectureHolder;
    }

}
