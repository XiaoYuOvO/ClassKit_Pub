package net.xiaoyu233.classkit.av;

import org.bytedeco.javacv.FrameRecorder;

public interface AudioListener extends AVListener {
    int getPriority();
    void onSample(AudioSample sample) throws FrameRecorder.Exception;

    void streamStart();
    void requestStop() throws FrameRecorder.Exception;

    boolean isStarted();
    boolean isStopped();

    void onStopped();
}
