package net.xiaoyu233.classkit.gui.table;

import net.xiaoyu233.classkit.api.Lesson;
import net.xiaoyu233.classkit.api.Subject;
import net.xiaoyu233.classkit.api.unit.Student;
import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.*;
import java.awt.*;

public class LessonCellRenderer extends DefaultTableCellRenderer {
    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;

    // We need a place to store the color the JLabel should be returned
    // to after its foreground and background colors have been set
    // to the selection background color.
    // These ivars will be made protected when their names are finalized.
    private Color unselectedForeground;
    private Color unselectedBackground;
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JTextArea textArea = new JTextArea();
        if (table == null) {
            return textArea;
        }

        Color fg = null;
        Color bg = null;

        JTable.DropLocation dropLocation = table.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsertRow()
                && !dropLocation.isInsertColumn()
                && dropLocation.getRow() == row
                && dropLocation.getColumn() == column) {

            fg = DefaultLookup.getColor(this, ui, "Table.dropCellForeground");
            bg = DefaultLookup.getColor(this, ui, "Table.dropCellBackground");

            isSelected = true;
        }

        if (isSelected) {
            textArea.setForeground(fg == null ? table.getSelectionForeground()
                                        : fg);
            textArea.setBackground(bg == null ? table.getSelectionBackground()
                                        : bg);
        } else {
            Color background = unselectedBackground != null
                    ? unselectedBackground
                    : table.getBackground();
            if (background == null || background instanceof javax.swing.plaf.UIResource) {
                Color alternateColor = DefaultLookup.getColor(this, ui, "Table.alternateRowColor");
                if (alternateColor != null && row % 2 != 0) {
                    background = alternateColor;
                }
            }
            textArea.setForeground(unselectedForeground != null
                                        ? unselectedForeground
                                        : table.getForeground());
            textArea.setBackground(background);
        }

        textArea.setFont(table.getFont());

        if (hasFocus) {
            Border border = null;
            if (isSelected) {
                border = DefaultLookup.getBorder(this, ui, "Table.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = DefaultLookup.getBorder(this, ui, "Table.focusCellHighlightBorder");
            }
            textArea.setBorder(border);

            if (!isSelected && table.isCellEditable(row, column)) {
                Color col;
                col = DefaultLookup.getColor(this, ui, "Table.focusCellForeground");
                if (col != null) {
                    textArea.setForeground(col);
                }
                col = DefaultLookup.getColor(this, ui, "Table.focusCellBackground");
                if (col != null) {
                    textArea.setBackground(col);
                }
            }
        } else {
            textArea.setBorder(getNoFocusBorder());
        }

        textArea.setText(value == null ? "" : value.toString());

        Lesson lesson = (Lesson) value;
        textArea.setForeground(Color.BLACK);
        if (lesson.getSubject() == Subject.NONE) {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.WHITE);
        }
        int maxPreferredHeight;
        for (int i = 0; i < table.getColumnCount(); i++) {
            textArea.setText(lesson.getSubject() + "\n"+lesson.getContent());
            textArea.setSize(table.getColumnModel().getColumn(column).getWidth(), 0);
        }

        maxPreferredHeight = (int) ((table.getParent().getSize().getHeight()) / (table.getRowCount()));

        if (table.getRowHeight(row) != maxPreferredHeight) {
            table.setRowHeight(row, Math.max(maxPreferredHeight,1));
        }

        textArea.setSize(table.getRowHeight(row),table.getColumnModel().getColumn(column).getWidth());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        return textArea;
    }

    private Border getNoFocusBorder() {
        Border border = DefaultLookup.getBorder(this, ui, "Table.cellNoFocusBorder");
        if (System.getSecurityManager() != null) {
            if (border != null) return border;
            return SAFE_NO_FOCUS_BORDER;
        } else if (border != null) {
            if (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER) {
                return border;
            }
        }
        return noFocusBorder;
    }
}
