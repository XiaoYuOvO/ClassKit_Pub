package net.xiaoyu233.classkit.util.profiler;

import java.util.ArrayList;
import java.util.List;

public class ProfileType {
    public static final List<ProfileType> TYPES = new ArrayList<>();
    public static final ProfileType GRABBING_TIME = new ProfileType("Grabbing time");
    public static final ProfileType AUDIO_PLAYBACK_TIME = new ProfileType("Audio playback time");
    public static final ProfileType AUDIO_QUEUE_SIZE = new ProfileType("Audio queue size");
    public static final ProfileType AUDIO_READ_TIME = new ProfileType("Audio read time");
    public static final ProfileType AUDIO_SOUND_PRESSURE = new ProfileType("Audio sound pressure");
    public static final ProfileType SOUND_AMPLIFIER = new ProfileType("Sound amplifier");
    private double lastAvg;
    private double lastValue;
    private long updatedCount;
    private double min,max;
    private final String name;
    private Runnable updateCallback;

    public ProfileType(String name) {
        this.name = name;
        TYPES.add(this);
    }

    public void setUpdateCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback;
    }

    public void updateValue(double time){
        lastValue = time;
        this.max = Math.max(this.max,time);
        this.min = Math.min(this.min,time);
        lastAvg = (lastAvg * updatedCount + time) / (updatedCount + 1);
        updatedCount++;
        if (this.updateCallback != null){
            this.updateCallback.run();
        }
    }

    public long getUpdatedCount() {
        return updatedCount;
    }

    public String getName() {
        return name;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getLastAvg() {
        return lastAvg;
    }

    public double getLastValue() {
        return lastValue;
    }
}
