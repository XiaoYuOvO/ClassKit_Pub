package net.xiaoyu233.classkit.tools;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import net.xiaoyu233.classkit.config.RecorderConfig;
import net.xiaoyu233.classkit.event.EventType;
import net.xiaoyu233.classkit.gui.table.TodayClassTable;
import net.xiaoyu233.classkit.managment.EventManager;

import javax.swing.*;
import java.awt.*;

public class LessonTableTool extends Tool<RecorderConfig>{
    private JFrame frame;
    public LessonTableTool() {

    }

    @Override
    public String getName() {
        return "Lesson Table";
    }

    @Override
    public void init(RecorderConfig config) {
        frame = new JFrame("");
        TodayClassTable comp = new TodayClassTable(config.getLessonTable());
        JScrollPane scrollPane = new JScrollPane(comp);
        frame.add(scrollPane);
        DisplayMode displayMode = frame.getGraphicsConfiguration().getDevice().getDisplayMode();
        frame.setBounds(0,0,displayMode.getWidth() / 8,displayMode.getHeight());
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    @Override
    public void reloadConfig(RecorderConfig config) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void registerEvent(EventManager eventManager) {
        super.registerEvent(eventManager);
        eventManager.registerListener(EventType.CLASS_OVER, (classOver) -> {
            this.frame.setVisible(true);
            this.frame.requestFocus();
            this.frame.setAlwaysOnTop(true);
        });
        eventManager.registerListener(EventType.CLASS_BEGIN, (classOver) -> this.frame.setVisible(false));
    }
}
