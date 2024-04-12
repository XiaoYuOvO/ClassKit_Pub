package net.xiaoyu233.classkit.gui.table;

import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.util.Fonts;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.util.EventObject;

public class ClassDisplay extends JTable {
    public ClassDisplay(ClassManager manager){
        super(new ClassTableModel(manager));
        JTableHeader tableHeader = this.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        this.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean shouldSelectCell(EventObject anEvent) {

                return false;
            }
        });
        this.setShowGrid(false);
        this.setShowVerticalLines(true);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setSelectionModel(new DefaultListSelectionModel(){
            @Override
            public boolean isSelectedIndex(int index) {
                return false;
            }
        });
        this.setCellSelectionEnabled(false);
        this.setFont(Fonts.BIG_FONT);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setDefaultRenderer(Student.class,new StudentCellRender());

        this.getTableHeader().setDefaultRenderer(new ClassTableHeaderRenderer());
//        scrollPane.setViewportView(this);
//        scrollPane.setBorder(BorderFactory.createLineBorder(Color.RED));
//        return scrollPane;
    }
}
