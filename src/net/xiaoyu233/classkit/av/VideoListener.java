package net.xiaoyu233.classkit.av;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

public interface VideoListener extends AVListener{
    void onFrame(Frame frame) throws FrameRecorder.Exception;
    void addProcessor(ImageProcessor processor);
    void streamStart();
    /**
     * Should not call audio/video IO to stop on this method!
     * */
    void requestStop() throws FrameRecorder.Exception;

    void onStopped();
    boolean isStarted();
    boolean isStopped();

    int getPriority();
}
