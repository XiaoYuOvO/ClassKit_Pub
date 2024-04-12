package net.xiaoyu233.classkit.av.processors;

import net.xiaoyu233.classkit.util.profiler.ProfileType;

import java.util.function.ToDoubleFunction;

public class SoundLevelNormalizer implements ToDoubleFunction<Double> {
    private double lastAmp = 1;
    private double splitThreshold = 20;
    private double targetSPL;
    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SoundLevelNormalizer(double targetSPL) {
        this.targetSPL = targetSPL;
    }

    public void setTargetSPL(double targetSPL) {
        this.targetSPL = targetSPL;
    }

    public double getSplitThreshold() {
        return splitThreshold;
    }

    public void setSplitThreshold(double splitThreshold) {
        this.splitThreshold = splitThreshold;
    }

    @Override
    public double applyAsDouble(Double value) {
        if (value < this.splitThreshold){
            lastAmp = 1;
            return 1;
        }
        if (this.enabled && !Double.isNaN(value) && value >= 0){
            double pow = Math.min(5,Math.pow(2, (targetSPL - value) / 10));
            pow = Math.min((lastAmp + pow) / 2, 5);
            ProfileType.SOUND_AMPLIFIER.updateValue(pow);
            lastAmp = pow;
            return pow;
        }
        return 1;
    }
}
