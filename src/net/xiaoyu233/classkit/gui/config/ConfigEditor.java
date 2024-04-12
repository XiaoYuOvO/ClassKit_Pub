package net.xiaoyu233.classkit.gui.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.xiaoyu233.classkit.config.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class ConfigEditor extends JFrame {
    static final Font TREE_FONT = new Font("Default", Font.PLAIN, 16);
    private final List<Runnable> saveRuns = new ArrayList<>();
    private final int width = 600,height = 550;
    private final EditorAdapter adapter = new EditorAdapter();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public ConfigEditor(List<ConfigRegistry> registries) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("所有配置");
        for (ConfigRegistry registry : registries) {
            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode("配置文件 " + registry.getPathToConfigFile().getName());
            root.add(defaultMutableTreeNode);
            this.addAllConfigs(registry.getRoot(),defaultMutableTreeNode);
        }
        TreeModel model = new DefaultTreeModel(root);
        JTree tree = new JTree(model);
        tree.setFont(TREE_FONT);
        tree.setExpandsSelectedPaths(true);
        tree.setRowHeight(TREE_FONT.getSize() + 5);
        tree.setCellRenderer(new DefaultTreeCellRenderer(){
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (!leaf){
                    return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                }else {
                    EditingComponent editingUnit = ((ConfigTreeNode) value).editingUnit;
                    editingUnit.setBackground(hasFocus ? this.getBackgroundSelectionColor() : this.getBackgroundNonSelectionColor());
                    return editingUnit;
                }
            }
        });
        tree.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
                if (lastSelectedPathComponent instanceof ConfigTreeNode){
                    Runnable saveChange = ((ConfigTreeNode) lastSelectedPathComponent).editingUnit.getSaveChange();
                    if (!saveRuns.contains(saveChange)){
                        saveRuns.add(saveChange);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(tree);
        this.setLayout(new BorderLayout());
        this.add(scrollPane,BorderLayout.NORTH);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Box optBox = Box.createHorizontalBox();
        JButton cancel = new JButton("取消");
        cancel.addActionListener(e -> ConfigEditor.this.dispose());
        optBox.add(cancel);
        optBox.add(Box.createVerticalGlue());
        JButton reset = new JButton("重置当前选项");
        reset.addActionListener(e -> {
            Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
            if (lastSelectedPathComponent instanceof ConfigTreeNode){
                ConfigTreeNode<?> node = (ConfigTreeNode<?>) lastSelectedPathComponent;
                node.setToDefault();
                node.editingUnit.repaint();
                tree.repaint();
            }
        });
        optBox.add(reset);
        optBox.add(Box.createHorizontalGlue());
        JButton save = new JButton("保存并退出");
        save.addActionListener(e -> {
            for (Runnable saveRun : this.saveRuns) {
                saveRun.run();
            }
            for (ConfigRegistry registry : registries) {
                try (FileWriter writer = new FileWriter(registry.getPathToConfigFile())){
                    GSON.toJson(registry.getRoot().write(),writer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this,"保存成功,更改已生效!");
            ConfigEditor.this.dispose();
        });
        optBox.add(save);
        this.add(optBox,BorderLayout.SOUTH);
        tree.setEditable(true);
        tree.setCellEditor(new DefaultCellEditor(new JTextField()){

            @Override
            public void cancelCellEditing() {
                super.cancelCellEditing();
                Runnable saveChange = ((ConfigTreeNode<?>) tree.getLastSelectedPathComponent()).editingUnit.getSaveChange();
                if (!saveRuns.contains(saveChange)){
                    saveRuns.add(saveChange);
                }
            }

            @Override
            public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
                if (!leaf){
                    if (!expanded){
                        tree.expandPath(tree.getSelectionPath());
                    }else {
                        tree.collapsePath(tree.getSelectionPath());
                    }
                }
                return tree.getCellRenderer().getTreeCellRendererComponent(tree,value,isSelected,expanded,leaf,row,true);
            }

            //Make can expand/collapse by double click
            @Override
            public boolean isCellEditable(EventObject anEvent) {
                if (anEvent instanceof MouseEvent) {
                    TreePath path = tree.getPathForLocation(
                            ((MouseEvent)anEvent).getX(),
                            ((MouseEvent)anEvent).getY());
                    if (path!=null) {
                        Object value = path.getLastPathComponent();
                        TreeModel treeModel = tree.getModel();
                        boolean leaf = treeModel.isLeaf(value);
                        if (!leaf){
                            if ( ((MouseEvent) anEvent).getClickCount() >= 1) {
                                if (tree.isExpanded(path)) {
                                    tree.collapsePath(path);
                                } else {
                                    tree.expandPath(path);
                                }
                            }
                            return false;
                        }else {
                            return true;
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean stopCellEditing() {
                Runnable saveChange = ((ConfigTreeNode<?>) tree.getLastSelectedPathComponent()).editingUnit.getSaveChange();
                if (!saveRuns.contains(saveChange)){
                    saveRuns.add(saveChange);
                }
                return super.stopCellEditing();
            }
        });
        DisplayMode displayMode = this.getGraphicsConfiguration().getDevice().getDisplayMode();
        this.setBounds((displayMode.getWidth() - this.width) / 2,(displayMode.getHeight() - this.height) / 2,this.width,this.height);
    }

    private void addAllConfigs(Config config, DefaultMutableTreeNode root){
        if (config instanceof ConfigCategory){
            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(config.getName());
            root.add(defaultMutableTreeNode);
            for (Config child : ((ConfigCategory) config).getChild()) {
                addAllConfigs(child,defaultMutableTreeNode);
            }
        } else if (config instanceof ConfigEntry){
            root.add(new ConfigTreeNode<>((ConfigEntry<?>) config,adapter));
        }
    }

    static class ConfigTreeNode<T> extends DefaultMutableTreeNode{
        private final ConfigEntry<T> configEntry;
        private final EditingComponent<T> editingUnit;

        ConfigTreeNode(ConfigEntry<T> configEntry,EditorAdapter adapter) {
            this.configEntry = configEntry;
            this.editingUnit = adapter.getEditorFor(configEntry);
        }

        public ConfigEntry<T> getConfigEntry() {
            return configEntry;
        }

        public EditingComponent<T> getEditingUnit() {
            return editingUnit;
        }

        public void setToDefault(){
            this.editingUnit.getReloadValue().accept(this.configEntry.getDefaultValue());
        }

        @Override
        public String toString() {
            return configEntry.getComment();
        }
    }


}
