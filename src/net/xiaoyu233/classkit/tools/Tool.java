package net.xiaoyu233.classkit.tools;

import net.xiaoyu233.classkit.config.ToolConfig;
import net.xiaoyu233.classkit.keys.KeyBind;
import net.xiaoyu233.classkit.managment.EventManager;
import net.xiaoyu233.classkit.managment.KeyManager;
import net.xiaoyu233.classkit.config.Config;
import net.xiaoyu233.classkit.managment.MessageManager;

import java.util.ArrayList;
import java.util.List;

public abstract class Tool<C extends ToolConfig> {
    private EventManager eventManager;
    private MessageManager messageManager = null;
    private final List<Integer> registeredKeyIDs = new ArrayList<>();
    public abstract String getName();
    public ConfiguredTool<Tool<C>,C> configureWith(C config){
        return new ConfiguredTool<>(this, config);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void registerEvent(EventManager eventManager){
        this.eventManager = eventManager;
    }
    public abstract void init(C config);
    public abstract void reloadConfig(C config);
    public abstract void tick();
    public void clear(){
        for (Integer registeredKeyID : this.registeredKeyIDs) {
            KeyManager.INSTANCE.removeShortcut(registeredKeyID);
        }
    };

    public void addKeyCallback(KeyBind keyBind,Runnable runnable){
        this.registeredKeyIDs.add(KeyManager.INSTANCE.addShortcut(keyBind,runnable));
    }


    protected void sendMessage(String msg){
        if (this.messageManager != null){
            messageManager.addMessage(msg);
            System.out.println(msg);
        }
    }

    public final Tool<C> setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
        return this;
    }
}
