package net.xiaoyu233.classkit;

import net.xiaoyu233.classkit.config.*;
import net.xiaoyu233.classkit.managment.MessageManager;
import net.xiaoyu233.classkit.managment.WenDaoManager;
import net.xiaoyu233.classkit.tools.ResolutionTool;
import net.xiaoyu233.classkit.tools.ScreenshotTool;
import net.xiaoyu233.classkit.managment.ToolManager;
import net.xiaoyu233.classkit.tools.VolumeTool;

import java.awt.*;

@Deprecated
//Old class opener, no capture or recording
public class ClassOpener {
    public static void main(String[] args) throws AWTException, InterruptedException {
        ToolManager manager = new ToolManager();
        MessageManager messageManager = new MessageManager();
        manager.registerTool(new ScreenshotTool().setMessageManager(messageManager).configureWith(EmptyConfig.INSTANCE));
        manager.registerTool(new VolumeTool().setMessageManager(messageManager).configureWith(new VolumeToolConfig()));
        manager.registerTool(new ResolutionTool().setMessageManager(messageManager).configureWith(new ResolutionConfig()));
//        manager.registerTool(new RecordTool().setMessageManager(messageManager).configureWith(new RecorderConfig()));
        WenDaoManager wenDaoManager = new WenDaoManager();
        wenDaoManager.createWenDaoProcess();
        manager.initAll();
        wenDaoManager.waitFor(manager::tickAll,1000);
        manager.cleanUpAll();
        System.exit(0);
    }
}
