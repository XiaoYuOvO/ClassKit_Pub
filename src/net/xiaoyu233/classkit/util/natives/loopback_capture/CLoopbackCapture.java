package net.xiaoyu233.classkit.util.natives.loopback_capture;

import javax.sound.sampled.AudioFormat;

public class CLoopbackCapture {
    private final LoopbackCaptureLibrary.CLoopbackCapture client;
    private boolean released;

    public CLoopbackCapture() {
        client = LoopbackCaptureLibrary.CreateCaptureClient();
    }

    private void checkReleased(){
        if (released){
            throw new IllegalStateException("Capture client has been released");
        }
    }

    public void startCapture(int pid, boolean includeProcessTree){
        LoopbackCaptureLibrary.StartCaptureAsync(client,pid, (byte) (includeProcessTree ? 1 : 0));
    }

    public boolean isRunning(){
        return LoopbackCaptureLibrary.IsRunning(client) == 1;
    }

    public void stopCapture() {
        LoopbackCaptureLibrary.StopCapture(client);
    }

    public void releaseClient(){
        LoopbackCaptureLibrary.ReleaseClient(client);
    }

    public CaptureAudioData getNextAudioData() {
        LoopbackCaptureLibrary.AudioData data = LoopbackCaptureLibrary.GetNextAudioData(client);
        data.read();
        return new CaptureAudioData(data);
    }

    public boolean hasAudioData() {
        return LoopbackCaptureLibrary.HasAudioData(client) == 1;
    }

    public void setAudioFormat(AudioFormat format){
        LoopbackCaptureLibrary.SetAudioFormat(client, (short) format.getChannels(),
                (int) (format.getSampleRate()), (short) format.getSampleSizeInBits());
    }

    public int getAvailableData(){
        return LoopbackCaptureLibrary.GetAvailableData(client);
    }
}
