package net.xiaoyu233.classkit;

import net.xiaoyu233.classkit.av.SwitchDataLine;
import net.xiaoyu233.classkit.av.WenDaoGrabber;
import net.xiaoyu233.classkit.config.*;
import net.xiaoyu233.classkit.managment.MessageManager;
import net.xiaoyu233.classkit.managment.ToolManager;
import net.xiaoyu233.classkit.managment.WenDaoManager;
import net.xiaoyu233.classkit.tools.*;
import net.xiaoyu233.classkit.util.Utils;
import net.xiaoyu233.classkit.util.natives.FindWindowEnhance;
import net.xiaoyu233.classkit.util.natives.loopback_capture.CaptureDataLine;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.AbstractArray;
import org.bytedeco.opencv.opencv_core.AbstractScalar;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ClassLauncher {
    public static void main(String[] args) throws AWTException, InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, LineUnavailableException {
        //Preload the classes to speed up the loading

        Loader.load(AbstractScalar.class);
        Loader.load(AbstractArray.class);
        Loader.load(opencv_imgproc.class);
        Loader.load(Mat.class);

        FFmpegLogCallback.set();
        ToolManager manager = new ToolManager();
        MessageManager messageManager = new MessageManager();
        manager.registerTool(new ScreenshotTool().setMessageManager(messageManager).configureWith(EmptyConfig.INSTANCE));
        FFmpegRecordTool fFmpegRecordTool = new FFmpegRecordTool();
        RecorderConfig recorderConfig = new RecorderConfig();
        manager.registerTool(fFmpegRecordTool.setMessageManager(messageManager).configureWith(recorderConfig));
        DisplayerTool displayerTool = new DisplayerTool();
        StreamingTool streamingTool = new StreamingTool();
        AudioPlaybackTool audioPlaybackTool = new AudioPlaybackTool();
        manager.registerTool(displayerTool.setMessageManager(messageManager).configureWith(new DisplayerConfig()));
        manager.registerTool(audioPlaybackTool.setMessageManager(messageManager).configureWith(new AudioPlaybackConfig()));
        manager.registerTool(streamingTool.setMessageManager(messageManager).configureWith(new StreamingConfig()));
        manager.registerTool(new LessonTableTool().configureWith(recorderConfig));
        WenDaoManager wenDaoManager = new WenDaoManager();
        manager.registerTool(wenDaoManager.configureWith(new WenDaoConfig()));
        try {
            CaptureDataLine captureDataLine = new CaptureDataLine();
            captureDataLine.setIncludeProcessTree(true);
            WenDaoGrabber grabber = new WenDaoGrabber("EducationUI", new SwitchDataLine(captureDataLine,Utils.TARGET_DATA_LINE));
            manager.registerTool(grabber.setMessageManager(messageManager).configureWith(EmptyConfig.INSTANCE));
            manager.initAll();
            audioPlaybackTool.registerThis(grabber);
            displayerTool.registerThis(grabber);
//            streamingTool.registerThis(grabber);
            fFmpegRecordTool.registerProvider(grabber);
            wenDaoManager.createWenDaoProcess();
            FindWindowEnhance.injectFindWindow();
            wenDaoManager.waitForLogin();
            captureDataLine.setPid(wenDaoManager.getPid());
            captureDataLine.setCurrentFormat(Utils.SOURCE_DATA_LINE.getFormat());
            grabber.start();
            grabber.scheduleGrabJob(new ScheduledThreadPoolExecutor(2));
            wenDaoManager.waitFor(manager::tickAll,1000);
            manager.cleanUpAll();
            System.exit(0);
        } catch (LineUnavailableException | FrameRecorder.Exception | FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }
}
