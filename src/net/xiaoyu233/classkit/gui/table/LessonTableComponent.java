package net.xiaoyu233.classkit.gui.table;

import net.xiaoyu233.classkit.api.Lesson;
import net.xiaoyu233.classkit.api.LessonTable;
import net.xiaoyu233.classkit.util.Fonts;
import net.xiaoyu233.classkit.util.Utils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class LessonTableComponent extends JTable {
    public LessonTableComponent(LessonTable lessonTable) {
        super(new LessonTableModel(lessonTable));
        this.setDefaultRenderer(Lesson.class,new LessonCellRenderer());
        JTableHeader tableHeader = this.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        tableHeader.setDefaultRenderer(new LessonTableHeaderRenderer());
//        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setFont(Fonts.MIDDLE_FONT);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setSelectionModel(new DefaultListSelectionModel(){
            @Override
            public boolean isSelectedIndex(int index) {
                return false;
            }
        });
        this.setCellSelectionEnabled(false);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                try {
                    Utils.fitTableColumns(LessonTableComponent.this);
                    LessonTableComponent.this.doLayout();
                    LessonTableComponent.this.repaint();
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    static class LessonTableModel extends AbstractTableModel {
        private final LessonTable lessonTable;
        public LessonTableModel(LessonTable lessonTable) {
            super();
            this.lessonTable = lessonTable;
        }

        @Override
        public int getRowCount() {
            if (this.lessonTable != null) {
                return this.lessonTable.getLessonCount();
            }
            return 0;
        }

        @Override
        public int getColumnCount() {
            return this.lessonTable.getDayCount();
        }

        @Override
        public Object getValueAt(int row, int column) {
            DayOfWeek day = DayOfWeek.values()[column];
            return this.lessonTable.getLessonsOf(day).get(row);
        }

        @Override
        public String getColumnName(int column) {
            return DayOfWeek.values()[column].getDisplayName(TextStyle.FULL, Locale.CHINA);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (aValue instanceof Lesson) {
                this.lessonTable.setLesson(DayOfWeek.values()[columnIndex], rowIndex, ((Lesson) aValue));
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Lesson.class;
        }
    }
}
