package net.xiaoyu233.classkit.gui.table;

import net.xiaoyu233.classkit.api.unit.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StudentCellRender extends DefaultTableCellRenderer {
    private static final Color GIRL = new Color(255, 47, 47, 255);
    private static final Color BOY = new Color(80, 120, 255, 255);
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(null);
        Student student = (Student) value;
        if (!student.getName().isEmpty()){
            setBorder( BorderFactory.createRaisedSoftBevelBorder());
            switch (student.getGender()) {
                case Boy:
                    this.setBackground(BOY);
                    this.setForeground(Color.BLACK);
                    break;
                case Girl:
                    this.setBackground(GIRL);
                    this.setForeground(Color.WHITE);
                    break;
                case None:
                    break;
            }
        }else {
            this.setBackground(Color.WHITE);
            this.setForeground(Color.WHITE);
        }
        int maxPreferredHeight = 0;
        for (int i = 0; i < table.getColumnCount(); i++) {
            setText(student.getName());
            setSize(table.getColumnModel().getColumn(column).getWidth(), 0);
            maxPreferredHeight = Math.max(maxPreferredHeight, getPreferredSize().height);
        }

        maxPreferredHeight = (int) (table.getSize().getHeight() / (table.getRowCount()));

        if (table.getRowHeight(row) != maxPreferredHeight) {
            table.setRowHeight(row, Math.max(maxPreferredHeight,1));
        }
//        table.setRowHeight(row, this.getMinimumSize().height);
        setVerticalAlignment(CENTER);
        setHorizontalAlignment(CENTER);
        return this;
    }
}
