package net.xiaoyu233.classkit.gui.config;

import net.xiaoyu233.classkit.config.ConfigEntry;

public interface EditorFactory<T> {
    EditingComponent<T> create(ConfigEntry<T> entry);
}
