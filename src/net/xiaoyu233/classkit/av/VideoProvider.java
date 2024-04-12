package net.xiaoyu233.classkit.av;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import javax.sound.sampled.LineUnavailableException;

public interface VideoProvider {
    void addVideoCallback(VideoListener callback);
    void removeVideoCallback(VideoListener callback);
    void start() throws FrameRecorder.Exception, FrameGrabber.Exception, LineUnavailableException;
    void stop() throws FrameGrabber.Exception;
    void loopVideo() throws FrameGrabber.Exception, FrameRecorder.Exception;
    double getGamma();
}
