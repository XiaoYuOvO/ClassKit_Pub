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
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class TodayClassTable  extends JTable{
    public TodayClassTable(LessonTable lessonTable) {
        super(new TodayClassTable.LessonTableModel(lessonTable));
        this.setDefaultRenderer(Lesson.class,new LessonCellRenderer());
        JTableHeader tableHeader = this.createDefaultTableHeader();
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        tableHeader.setDefaultRenderer(new LessonTableHeaderRenderer());
        this.setTableHeader(tableHeader);
//        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setFont(Fonts.MIDDLE_FONT);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                try {
//                    Utils.fitTableColumns(TodayClassTable.this);
                    TodayClassTable.this.doLayout();
                    TodayClassTable.this.repaint();
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
            return 1;
        }

        @Override
        public Object getValueAt(int row, int column) {
            DayOfWeek day = DayOfWeek.from(LocalDate.now());
            return this.lessonTable.getLessonsOf(day).get(row);
        }

        @Override
        public String getColumnName(int column) {
            return DayOfWeek.from(LocalDate.now()).getDisplayName(TextStyle.FULL, Locale.CHINA);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (aValue instanceof Lesson) {
                this.lessonTable.setLesson(DayOfWeek.from(LocalDate.now()), rowIndex, ((Lesson) aValue));
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Lesson.class;
        }
    }
}
