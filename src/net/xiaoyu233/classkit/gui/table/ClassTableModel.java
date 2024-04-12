package net.xiaoyu233.classkit.gui.table;

import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.pos.CoordPosition;
import net.xiaoyu233.classkit.api.pos.GroupDeskPosition;
import net.xiaoyu233.classkit.api.pos.Side;
import net.xiaoyu233.classkit.api.unit.Student;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class ClassTableModel extends AbstractTableModel {
    private final ClassManager manager;;

    public ClassTableModel(ClassManager manager) {
        this.manager = manager;
    }

    @Override
    public int getRowCount() {
        return this.manager.getClassSize().getMaxGroupSize();
    }

    @Override
    public int getColumnCount() {
        int size = manager.getGroups().size();
        return size * 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return switch ((columnIndex + 1) % 3) {
            case 0 -> "过道";
            case 1 -> "左";
            case 2 -> "右";
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Student.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
//        if ((columnIndex + 1) % 3 == 0){
//            return Student.EMPTY;
//        }
        int group = columnIndex / 2;
        if (rowIndex < this.manager.getGroups().get(group).size()) {
            return this.manager.getStudent(new GroupDeskPosition(group,rowIndex, Side.values()[columnIndex % 2] ));
        }else {
            return Student.EMPTY;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }
}
