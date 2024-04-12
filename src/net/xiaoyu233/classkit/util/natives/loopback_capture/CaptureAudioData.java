package net.xiaoyu233.classkit.util.natives.loopback_capture;

import java.lang.ref.Cleaner;

public class CaptureAudioData {
    static class State implements Runnable {
        private volatile boolean released;
        private final LoopbackCaptureLibrary.AudioData data;
        State(LoopbackCaptureLibrary.AudioData data) {
            this.data = data;
        }

        public synchronized void run() {
            if (!released){
                released = true;
            }
        }
    }
    private final LoopbackCaptureLibrary.AudioData data;

    private static final Cleaner cleaner =  Cleaner.create();
    CaptureAudioData(LoopbackCaptureLibrary.AudioData data) {
        this.data = data;
        cleaner.register(this,new State(data));
    }

    public byte[] getBuffer(){
        data.read();
        if (data.length < 0){
            data.readField("length");
        }
        byte[] result;
        try {
            result  = data.data.getByteArray(0, data.length);
        }catch (NegativeArraySizeException | NullPointerException e){
            result = new byte[0];
        }
        return result;
    }

    public int getLength(){
        return data.length;
    }

    public void releaseData(){
        LoopbackCaptureLibrary.ReleaseAudioData(this.data);
    }
}
