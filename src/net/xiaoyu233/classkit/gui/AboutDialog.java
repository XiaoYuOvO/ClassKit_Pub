package net.xiaoyu233.classkit.gui;

import net.xiaoyu233.classkit.util.Constant;
import net.xiaoyu233.classkit.util.Fonts;
import net.xiaoyu233.classkit.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AboutDialog extends JDialog {
    private static Map<JFrame,AboutDialog> aboutDialogMap = new HashMap<>();
    private AboutDialog(JFrame owner, String version,String buildDate, String programName) {
        super(owner);
        this.setTitle("关于" + programName);
        this.setLayout(new BorderLayout());
        Box contentBox = Box.createVerticalBox();
        JLabel programNameLabel = new JLabel(programName);
        programNameLabel.setFont(Fonts.MIDDLE_BOLD_FONT);
        contentBox.add(programNameLabel);
        contentBox.add(new JLabel(""));
        contentBox.add(new JLabel("当前版本:" + version));
        contentBox.add(new JLabel("构建日期:" + buildDate));
        contentBox.add(new JLabel("版权所有 © 2020-2021 XiaoYu233"));
        this.add(contentBox);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setModal(true);
        this.pack();
        Utils.centerizeWindow(this);
    }

    public static void showForPosManager(PosManagerWindow posManager){
        aboutDialogMap.computeIfAbsent(posManager,jFrame -> new AboutDialog(jFrame, Constant.POS_MANAGER_VERSION,Constant.BUILD_DATE,"座位管理器")).setVisible(true);

    }
}
