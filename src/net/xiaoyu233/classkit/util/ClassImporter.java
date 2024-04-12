package net.xiaoyu233.classkit.util;

import net.xiaoyu233.classkit.api.LessonTable;
import net.xiaoyu233.classkit.config.RecorderConfig;
import net.xiaoyu233.classkit.gui.table.LessonTableComponent;
import net.xiaoyu233.classkit.io.LessonTableReader;
import java.io.*;

import javax.swing.*;

public class ClassImporter {
    public static void main(String[] args) throws IOException{
        LessonTable lessonTable = new RecorderConfig().getLessonTable();
        JFrame jFrame = new JFrame();
        LessonTableComponent component = new LessonTableComponent(lessonTable);
        jFrame.add(component);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
