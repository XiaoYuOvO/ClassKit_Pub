package net.xiaoyu233.classkit;

import net.xiaoyu233.classkit.av.ResourcePlayerGrabber;
import net.xiaoyu233.classkit.av.SwitchDataLine;
import net.xiaoyu233.classkit.config.AudioPlaybackConfig;
import net.xiaoyu233.classkit.config.DisplayerConfig;
import net.xiaoyu233.classkit.config.EmptyConfig;
import net.xiaoyu233.classkit.managment.MessageManager;
import net.xiaoyu233.classkit.managment.ResourcePlayerManager;
import net.xiaoyu233.classkit.managment.ToolManager;
import net.xiaoyu233.classkit.tools.AudioPlaybackTool;
import net.xiaoyu233.classkit.tools.DisplayerTool;
import net.xiaoyu233.classkit.tools.ScreenshotTool;
import net.xiaoyu233.classkit.util.Utils;
import net.xiaoyu233.classkit.util.natives.FindWindowEnhance;
import net.xiaoyu233.classkit.util.natives.loopback_capture.CaptureDataLine;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ResourcePlayerLauncher {
    public static void main(String[] args) throws AWTException, InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, LineUnavailableException {
        FFmpegLogCallback.set();
        ToolManager manager = new ToolManager();
        MessageManager messageManager = new MessageManager();
        manager.registerTool(new ScreenshotTool().setMessageManager(messageManager).configureWith(EmptyConfig.INSTANCE));
        DisplayerTool displayerTool = new DisplayerTool();
        AudioPlaybackTool audioPlaybackTool = new AudioPlaybackTool();
        manager.registerTool(displayerTool.setMessageManager(messageManager).configureWith(new DisplayerConfig()));
        manager.registerTool(audioPlaybackTool.setMessageManager(messageManager).configureWith(new AudioPlaybackConfig()));
        ResourcePlayerManager resourcePlayerManager = new ResourcePlayerManager();
        manager.initAll();
        try {
            CaptureDataLine captureDataLine = new CaptureDataLine();
            captureDataLine.setIncludeProcessTree(true);
            ResourcePlayerGrabber grabber = new ResourcePlayerGrabber("WebSeat_ZYLB_PlayerMainFrame",  new SwitchDataLine(captureDataLine,Utils.TARGET_DATA_LINE));
            audioPlaybackTool.registerThis(grabber);
            displayerTool.registerThis(grabber);
            FindWindowEnhance.injectFindWindow();
            resourcePlayerManager.waitForLaunch();
            captureDataLine.setPid(resourcePlayerManager.getPid());
            captureDataLine.setCurrentFormat(Utils.SOURCE_DATA_LINE.getFormat());
            grabber.start();
            grabber.scheduleGrabJob(new ScheduledThreadPoolExecutor(2));
            resourcePlayerManager.waitFor(manager::tickAll,1000);
            manager.cleanUpAll();
            System.exit(0);
        } catch (LineUnavailableException | FrameRecorder.Exception | FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }
}
