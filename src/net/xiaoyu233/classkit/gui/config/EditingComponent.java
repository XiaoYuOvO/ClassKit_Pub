package net.xiaoyu233.classkit.gui.config;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

class EditingComponent<T> extends Box {
    private final Consumer<T> reloadValue;
    private final Runnable saveChange;

    EditingComponent(JComponent editor, String name, Consumer<T> reloadValue,Runnable saveChange) {
        super(BoxLayout.X_AXIS);
        this.reloadValue = reloadValue;
        this.saveChange = saveChange;
        JLabel comp1 = new JLabel(name);
        comp1.setFont(ConfigEditor.TREE_FONT);
        this.add(comp1);
        this.add(Box.createHorizontalGlue());
        editor.setFont(ConfigEditor.TREE_FONT);
        this.add(editor);
    }

    public Consumer<T> getReloadValue() {
        return reloadValue;
    }

    public Runnable getSaveChange() {
        return saveChange;
    }

    @Override
    public void paint(Graphics g) {
        if (this.getBackground() != null) {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paint(g);
    }
}
