package net.xiaoyu233.classkit.gui.table;

import net.xiaoyu233.classkit.util.Fonts;
import sun.swing.table.DefaultTableCellHeaderRenderer;

import javax.swing.*;
import java.awt.*;

public class LessonTableHeaderRenderer extends DefaultTableCellHeaderRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        this.setHorizontalAlignment(CENTER);
        this.setVerticalAlignment(CENTER);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.setFont(Fonts.MIDDLE_FONT);
        return this;
    }
}
