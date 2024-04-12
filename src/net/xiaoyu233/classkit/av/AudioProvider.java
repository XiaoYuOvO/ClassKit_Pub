package net.xiaoyu233.classkit.av;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import javax.sound.sampled.LineUnavailableException;

public interface AudioProvider {
    void start() throws FrameRecorder.Exception, FrameGrabber.Exception, LineUnavailableException;
    void addAudioCallback(AudioListener callback);
    void removeAudioCallback(AudioListener callback);
    void stop() throws FrameGrabber.Exception;
    void loopAudio() throws FrameRecorder.Exception;
}
