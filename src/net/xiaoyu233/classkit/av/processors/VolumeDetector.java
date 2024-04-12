package net.xiaoyu233.classkit.av.processors;

import net.xiaoyu233.classkit.util.profiler.ProfileType;

import java.util.function.ToDoubleFunction;

public class VolumeDetector implements ToDoubleFunction<Double> {
    @Override
    public double applyAsDouble(Double value) {
        if (!Double.isNaN(value)){
            ProfileType.AUDIO_SOUND_PRESSURE.updateValue(value);
        }
        return 1;
    }
}
