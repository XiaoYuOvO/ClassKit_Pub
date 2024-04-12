package net.xiaoyu233.classkit.config;

import net.xiaoyu233.classkit.util.FieldReference;
import net.xiaoyu233.classkit.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PosManagerConfig {
    private final FieldReference<Boolean> keepGenders = new FieldReference<>(false);
    private final ConfigCategory root = ConfigCategory.of("root").
            addEntry(ConfigCategory.of("PosManager").
                             addEntry(ConfigCategory.of("PosChanging").
                                              addEntry(ConfigEntry.of("keepGenders",keepGenders).
                                                               withComment("保持性别分布不变"))));
    private final File configFile;
    private final JDialog configDialog;
    private final List<Runnable> confirmedRuns = new ArrayList<>();

    public PosManagerConfig(File configFile,JFrame owner) {
        this.configFile = configFile;
        this.readConfig();
        this.configDialog = createConfigDialog(owner);
    }

    private JCheckBox createBooleanSetter(String tip,FieldReference<Boolean> field){
        JCheckBox checkBox = new JCheckBox(tip);
        checkBox.setSelected(field.get());
        this.confirmedRuns.add(()-> field.set(checkBox.isSelected()));
        return checkBox;
    }

    private JDialog createConfigDialog(JFrame owner){
        JDialog dialog = new JDialog(owner);
        dialog.setTitle("设置");
        dialog.setLayout(new BorderLayout());
        Box verticalBox = Box.createVerticalBox();
        //Settings
        {
            verticalBox.add(this.createBooleanSetter("保持性别分布不变",keepGenders));
        }
        verticalBox.add(Box.createVerticalGlue());
        Box controlBox = Box.createHorizontalBox();
        controlBox.add(Box.createHorizontalGlue());
        JButton confirmButton = new JButton("保存退出");
        confirmButton.addActionListener(e -> {
            this.confirmedRuns.forEach(Runnable::run);
            this.writeConfig();
            dialog.setVisible(false);
        });
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.setVisible(false));
        controlBox.add(confirmButton);
        controlBox.add(cancelButton);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dialog.add(verticalBox,BorderLayout.NORTH);
        dialog.add(controlBox,BorderLayout.SOUTH);
        return dialog;
    }

    public void readConfig(){
        this.root.readFromFile(configFile);
    }

    public void writeConfig() {
        this.root.writeToFile(configFile);
    }

    public void showConfigDialog() {
        configDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        configDialog.setModal(true);
        configDialog.pack();
        Utils.centerizeWindow(configDialog);
        configDialog.setVisible(true);
    }

    public boolean keepGender(){
        return keepGenders.get();
    }
}
