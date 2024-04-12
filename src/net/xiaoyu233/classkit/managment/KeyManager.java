package net.xiaoyu233.classkit.managment;

import com.melloware.jintellitype.JIntellitype;
import net.xiaoyu233.classkit.keys.KeyBind;

import java.util.HashMap;
import java.util.Map;

public class KeyManager {
    private int id = 0;
    private static final JIntellitype jintellitype = JIntellitype.getInstance();
    public static final KeyManager INSTANCE = new KeyManager();
    private final Map<Integer,Runnable> callbacks = new HashMap<>();

    private KeyManager() {
        jintellitype.addHotKeyListener(i -> {
            try {
                this.callbacks.get(i).run();
            }catch (Throwable e){
                e.printStackTrace();
            }
        });
    }

    public int addShortcut(KeyBind keyBind, Runnable callback){
        jintellitype.registerHotKey(id,keyBind.getKeyModifier(),keyBind.getKeyId());
        this.callbacks.put(id,callback);
        id++;
        return id;
    }

    public void removeShortcut(int id){
        this.callbacks.remove(id);
        jintellitype.unregisterHotKey(id);

    }
}
