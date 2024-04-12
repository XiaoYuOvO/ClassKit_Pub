package net.xiaoyu233.classkit.av.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public class SilenceDetector implements ToDoubleFunction<Double> {
    private double threshold;
    private final List<Consumer<Boolean>> silenceCallbacks = new ArrayList<>();

    public SilenceDetector(double threshold) {
        this.threshold = threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public void addSilenceCallback(Consumer<Boolean> silenceCallback){
        this.silenceCallbacks.add(silenceCallback);
    }

    @Override
    public double applyAsDouble(Double value) {
        if (value < this.threshold){
            this.silenceCallbacks.forEach(callback -> callback.accept(true));
            return 0;
        }
        this.silenceCallbacks.forEach(callback -> callback.accept(false));
        return 1;
    }
}
