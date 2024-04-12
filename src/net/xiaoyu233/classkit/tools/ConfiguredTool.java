package net.xiaoyu233.classkit.tools;

import net.xiaoyu233.classkit.config.ToolConfig;
import net.xiaoyu233.classkit.managment.EventManager;

public class ConfiguredTool<T extends Tool<C>,C extends ToolConfig> {
    private final T tool;
    private C config;
    public ConfiguredTool(T tool, C config) {
        this.tool = tool;
        this.config = config;
    }

    public C getConfig(){
        return config;
    }

    public void reload(){
        config.reload();
//        tool.clear();
        tool.configureWith(config);
    }

    public void init(){
        this.tool.init(config);
    }

    public void registerEvents(EventManager eventManager){
        this.tool.registerEvent(eventManager);
    }

    public void tick(){
        this.tool.tick();
    }

    public void cleanUp(){
        this.tool.clear();
    }
}
