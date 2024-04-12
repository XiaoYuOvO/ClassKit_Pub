package net.xiaoyu233.classkit.keys;

import com.melloware.jintellitype.HotkeyListener;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public interface IKeyListenerWindow {
    void addHotKeyListener(HotkeyListener eventProcessor);
}
