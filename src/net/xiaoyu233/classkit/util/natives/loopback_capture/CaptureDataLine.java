package net.xiaoyu233.classkit.util.natives.loopback_capture;


import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class CaptureDataLine implements TargetDataLine {
    private static final Line.Info INFO = new Line.Info(CaptureDataLine.class);
    private CLoopbackCapture client;
    private int pid;
    private boolean includeProcessTree;
    private long framePos;
    private long msPos;
    private final List<LineListener> listeners = new ArrayList<>();
    private AudioFormat currentFormat = new AudioFormat(48000,16,2,false, true);
    public CaptureDataLine() {
        client = new CLoopbackCapture();
    }

    @Override
    public void open(AudioFormat format, int bufferSize) {
        setCurrentFormat(format);
        start();
    }

    public void setIncludeProcessTree(boolean includeProcessTree) {
        this.includeProcessTree = includeProcessTree;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public void open(AudioFormat format) {
        setCurrentFormat(format);
        start();
    }

    public void setCurrentFormat(AudioFormat currentFormat) {
        client.setAudioFormat(currentFormat);
        this.currentFormat = currentFormat;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        int byteRead = 0;
        while (byteRead < Math.min(len,b.length)){
            CaptureAudioData nextAudioData = client.getNextAudioData();
            byte[] buffer = nextAudioData.getBuffer();
            if (buffer.length == 0){
                return byteRead;
            }
            int copyCount = Math.min(buffer.length, b.length - byteRead);
            System.arraycopy(buffer,0,b,byteRead + off, copyCount);
            if (copyCount < buffer.length){
                throw new IllegalArgumentException("Output buffer length is not the integral time of the frame, causing sample drop");
            }
            framePos++;
            byteRead += buffer.length;
            nextAudioData.releaseData();
        }
        return byteRead;
    }

    @Override
    public void drain() {
        while (isOpen() && client.hasAudioData()) {
            client.getNextAudioData();
            framePos++;
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void start() {

        if (!this.client.isRunning()){
            this.msPos = System.currentTimeMillis();
            client.startCapture(pid, includeProcessTree);
            triggerListeners(LineEvent.Type.START);
        }
    }

    @Override
    public void stop() {
        client.stopCapture();
        triggerListeners(LineEvent.Type.STOP);
    }

    @Override
    public boolean isRunning() {
        return client.isRunning();
    }

    @Override
    public boolean isActive() {
        return client.isRunning();
    }

    @Override
    public AudioFormat getFormat() {
        return currentFormat;
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public int available() {
        return this.client.getAvailableData();
    }

    @Override
    public int getFramePosition() {
        return (int) framePos;
    }

    @Override
    public long getLongFramePosition() {
        return framePos;
    }

    @Override
    public long getMicrosecondPosition() {
        return msPos;
    }

    @Override
    public float getLevel() {
        return 0;
    }

    @Override
    public Line.Info getLineInfo() {
        return INFO;
    }

    @Override
    public void open() {
        triggerListeners(LineEvent.Type.OPEN);
    }

    @Override
    public void close() {
        stop();
        this.client.releaseClient();
        triggerListeners(LineEvent.Type.CLOSE);
    }

    @Override
    public boolean isOpen() {
        return isRunning();
    }

    @Override
    public Control[] getControls() {
        return new Control[0];
    }

    @Override
    public boolean isControlSupported(Control.Type control) {
        return false;
    }

    @Override
    public Control getControl(Control.Type control) {
        throw new IllegalArgumentException("Controls are not supported in loopback capture");
    }

    @Override
    public void addLineListener(LineListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeLineListener(LineListener listener) {
        this.listeners.remove(listener);
    }

    private void triggerListeners(LineEvent.Type type){
        this.listeners.forEach(listener -> listener.update(new LineEvent(this, type, framePos)));
    }

    @Override
    public String toString() {
        return "CaptureDataLine{" +
                "pid=" + pid +
                ", includeProcessTree=" + includeProcessTree +
                ", currentFormat=" + currentFormat +
                '}';
    }
}
