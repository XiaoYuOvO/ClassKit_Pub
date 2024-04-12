package net.xiaoyu233.classkit.gui.dialog;

import net.xiaoyu233.classkit.util.natives.FindWindowEnhance;
import net.xiaoyu233.classkit.util.natives.WindowsUtils;

import javax.swing.*;
import java.awt.*;

public class MessageWindow extends JDialog {
    private static final Font DEFAULT_FONT = new Font("Default", Font.PLAIN,30);
    private static final int HEIGHT = 40,WEIGHT = 1000;
    private final JLabel msgLabel = new JLabel();
    private String msg;
    public MessageWindow(){
        this.setUndecorated(true);
        this.setLayout(new BorderLayout());
        this.msgLabel.setFont(DEFAULT_FONT);
//        this.msgLabel.setBackground(new Color(255,0,0,255));
        this.msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(msgLabel,BorderLayout.CENTER);
        this.setForeground(new Color(6, 0, 0, 0));
        this.setBackground(new Color(6, 0, 0, 0));
        this.setAlwaysOnTop(true);
        this.setVisible(true);
//        this.setBounds();
    }

    public void setMsg(String msg){
        this.msg = msg;
        this.msgLabel.setText(msg);
        DisplayMode displayMode = this.getGraphicsConfiguration().getDevice().getDisplayMode();
        this.setBounds((int) (displayMode.getWidth() / WindowsUtils.getZoom() / 2 - WEIGHT / 2), (int) (displayMode.getHeight() / WindowsUtils.getZoom() *0.85f - HEIGHT/2),WEIGHT,HEIGHT);
    }
}
