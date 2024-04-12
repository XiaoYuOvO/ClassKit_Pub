package net.xiaoyu233.classkit.av;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class AudioSample {
    private final ShortBuffer data;
    private final int sampleRate;
    private final int channelCount;
    private final long timestamp;

    public AudioSample(ShortBuffer data, int sampleRate, int channelCount, long timestamp) {
        this.data = data;
        this.sampleRate = sampleRate;
        this.channelCount = channelCount;
        this.timestamp = timestamp;
    }

    @Override
    public AudioSample clone() {
        short[] array = data.array();
        return new AudioSample(ShortBuffer.wrap(Arrays.copyOf(array, array.length)),sampleRate,channelCount,timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static AudioSample readFromDataLine(TargetDataLine dataLine,int framerate){
        AudioFormat format = dataLine.getFormat();
        int channels = format.getChannels();
        float sampleRate = format.getSampleRate();
        byte[] audioBytes = new byte[(int) (sampleRate * channels) / framerate];
        int nBytesRead = dataLine.read(audioBytes, 0, Math.min(dataLine.available(), audioBytes.length));
        int nSamplesRead = nBytesRead / 2;
        short[] samples = new short[nSamplesRead];
        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
        ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

        return new AudioSample(sBuff, (int) sampleRate, channels, System.currentTimeMillis());
    }

    public ShortBuffer getData() {
        return data;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannelCount() {
        return channelCount;
    }
}

