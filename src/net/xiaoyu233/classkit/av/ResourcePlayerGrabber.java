package net.xiaoyu233.classkit.av;

import net.xiaoyu233.classkit.util.TaskThread;
import net.xiaoyu233.classkit.util.profiler.ProfilingMonitor;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.awt.*;

public class ResourcePlayerGrabber extends WenDaoGrabber{
    public ResourcePlayerGrabber(String title, TargetDataLine mixer) throws LineUnavailableException {
        super(title, new SwitchDataLine(mixer));
    }

    @Override
    public void start() throws FrameRecorder.Exception, FrameGrabber.Exception, LineUnavailableException {
        monitor = new ProfilingMonitor();
        EventQueue.invokeLater(()->monitor.setVisible(true));
        grabber.setFormat("gdigrab");
        grabber.setFrameRate(this.frameRate);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float zoom = 1;
        grabber.setOption("draw_mouse","0");
        grabber.setOption("framerate",String.valueOf(this.frameRate));
        grabber.setOption("offset_x", String.valueOf(0));
        grabber.setOption("offset_y",String.valueOf(28));
        grabber.setImageWidth(1366);
        grabber.setImageHeight(790);
        grabber.start();
        this.threadMap.values().forEach(TaskThread::start);
        dataLine.open(dataLine.getFormat());
        dataLine.start();
        for (AVListener videoListener : this.newCallbacks) {
            this.threadMap.get(videoListener).enqueueTask(videoListener::streamStart);
        }
        this.newCallbacks.clear();
        started = true;
    }
}
