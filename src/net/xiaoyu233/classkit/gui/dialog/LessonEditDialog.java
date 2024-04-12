package net.xiaoyu233.classkit.gui.dialog;

import net.xiaoyu233.classkit.api.Lesson;
import net.xiaoyu233.classkit.api.Subject;
import net.xiaoyu233.classkit.util.Fonts;
import net.xiaoyu233.classkit.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class LessonEditDialog extends JDialog {
    private final Lesson lesson;

    public LessonEditDialog(Lesson lesson, Consumer<Lesson> editCallback) {
        this.lesson = lesson;
        this.setFont(Fonts.MIDDLE_FONT);
        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        Box root = Box.createVerticalBox();
        JPanel edit = new JPanel();
        edit.setLayout(new GridLayout(2,0));
        JComboBox<Subject> subject = new JComboBox<>(Subject.values());
        subject.setSelectedItem(lesson.getSubject());
        subject.setFont(Fonts.MIDDLE_FONT);
//        subject.setPreferredSize(new Dimension(80,20));
        edit.add(subject);
        JTextField content = new JTextField(lesson.getContent());
        content.setFont(Fonts.MIDDLE_FONT);
        content.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    editCallback.accept(new Lesson(LessonEditDialog.this.lesson.getStartEndTime(),LessonEditDialog.this.lesson.getIndexOfDay(), ((Subject) subject.getSelectedItem()),content.getText(),
                            false));
                    LessonEditDialog.this.setVisible(false);
                    LessonEditDialog.this.dispose();
                }
            }
        });
        edit.add(content);
        root.add(edit);
        Box control = Box.createHorizontalBox();
        JButton cancel = new JButton("取消");
        cancel.setFont(Fonts.MIDDLE_FONT);
        cancel.addActionListener((e)->{
            LessonEditDialog.this.setVisible(false);
            LessonEditDialog.this.dispose();
        });
        control.add(cancel);
        control.add(Box.createHorizontalGlue());
        JButton ok = new JButton("确定");
        ok.setFont(Fonts.MIDDLE_FONT);
        ok.addActionListener(e -> {
            editCallback.accept(new Lesson(this.lesson.getStartEndTime(),this.lesson.getIndexOfDay(), ((Subject) subject.getSelectedItem()),content.getText(),
                    false));
            LessonEditDialog.this.setVisible(false);
            LessonEditDialog.this.dispose();
        });
        control.add(ok);
        root.add(Box.createVerticalGlue());
        root.add(control);
        this.add(root);
        this.setSize(new Dimension(300,200));
        Utils.centerizeWindow(this);
        this.pack();
        content.setFocusable(true);
        subject.setFocusable(false);
        this.setVisible(true);
        content.setFocusCycleRoot(true);
        content.setRequestFocusEnabled(true);
        content.requestFocus();
        content.grabFocus();
        subject.setRequestFocusEnabled(false);
        subject.setFocusCycleRoot(false);
    }
}
