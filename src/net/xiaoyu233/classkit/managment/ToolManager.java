package net.xiaoyu233.classkit.managment;

import net.xiaoyu233.classkit.config.ToolConfig;
import net.xiaoyu233.classkit.tools.ConfiguredTool;
import net.xiaoyu233.classkit.tools.Tool;

import java.util.ArrayList;
import java.util.List;

public class ToolManager {
    private final EventManager eventManager = new EventManager();
    private final List<ConfiguredTool<? extends Tool<?>,? extends ToolConfig>> tools = new ArrayList<> ();
    public <C extends ToolConfig> void registerTool(ConfiguredTool<? extends Tool<C>,C> tool) {
        tools.add(tool);
    }

    public void initAll(){
        for (ConfiguredTool<? extends Tool<?>, ? extends ToolConfig> tool : tools) {
            tool.init();
            tool.registerEvents(eventManager);
        }
    }

    public void reloadAll(){
        for (ConfiguredTool<? extends Tool<?>, ? extends ToolConfig> tool : this.tools) {
            tool.reload();
        }
    }



    public void cleanUpAll(){
        for (ConfiguredTool<? extends Tool<?>, ? extends ToolConfig> tool : this.tools) {
            tool.cleanUp();
        }
    }

    public void tickAll() {
        for (ConfiguredTool<? extends Tool<?>, ? extends ToolConfig> tool : this.tools) {
            tool.tick();
        }
    }
}
