package net.xiaoyu233.classkit.gui.table;

import net.xiaoyu233.classkit.api.ChangeHistory;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

public class ChangeHistoryTableModel extends DefaultTableModel {
    private List<ChangeHistory> histories;

    public void setHistories(List<ChangeHistory> histories) {
        this.histories = histories;
    }

    public ChangeHistoryTableModel(List<ChangeHistory> histories) {
        super(3,histories.size());
        this.histories = histories;
    }

    @Override
    public int getRowCount() {
        if (this.histories != null) {
            return this.histories.size();
        }
        return 0;

    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> "时间";
            case 1 -> "从";
            case 2 -> "到";
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.histories.get(rowIndex).toStringArray()[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }
}
