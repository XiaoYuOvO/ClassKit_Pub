package net.xiaoyu233.classkit.av.processors;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public class VolumeProcessor implements AudioProcessor {
    public static final double DEFAULT_SILENCE_THRESHOLD = -70.0;//db

    private final List<ToDoubleFunction<Double>> volumeCallback = new ArrayList<>();
    private final GainProcessor gainProcessor = new GainProcessor(1);

    /**
     * Create a new silence detector with a default threshold.
     */
    public VolumeProcessor(){

    }

    public void addListener(ToDoubleFunction<Double> callback){
        this.volumeCallback.add(callback);
    }

    /**
     * Calculates the local (linear) energy of an audio buffer.
     *
     * @param buffer
     *            The audio buffer.
     * @return The local (linear) energy of an audio buffer.
     */
    private double localEnergy(final float[] buffer) {
        double power = 0.0D;
        for (float element : buffer) {
            power += element * element;
        }
        return power;
    }

    /**
     * Returns the dBSPL for a buffer.
     *
     * @param buffer
     *            The buffer with audio information.
     * @return The dBSPL level for the buffer.
     */
    private double soundPressureLevel(final float[] buffer) {
        double value = Math.pow(localEnergy(buffer), 0.5);
        value = value / buffer.length;
        return linearToDecibel(value);
    }

    /**
     * Converts a linear to a dB value.
     *
     * @param value
     *            The value to convert.
     * @return The converted value.
     */
    private double linearToDecibel(final double value) {
        return 20.0 * Math.log10(value / 2e-5);
    }

    double currentSPL = 0;
    public double currentSPL(){
        return currentSPL;
    }

    /**
     * Checks if the dBSPL level in the buffer falls below a certain threshold.
     *
     * @param buffer
     *            The buffer with audio information.
     * @param silenceThreshold
     *            The threshold in dBSPL
     * @return True if the audio information in buffer corresponds with silence,
     *         false otherwise.
     */
    public boolean isSilence(final float[] buffer, final double silenceThreshold) {
        currentSPL = soundPressureLevel(buffer);
        return currentSPL < silenceThreshold;
    }

    public boolean isSilence(final float[] buffer) {
        return isSilence(buffer, DEFAULT_SILENCE_THRESHOLD);
    }


    @Override
    public boolean process(AudioEvent audioEvent) {
        currentSPL = soundPressureLevel(audioEvent.getFloatBuffer());
        double volumeMultiplier = 1.0d;
        for (ToDoubleFunction<Double> silenceCallback : this.volumeCallback) {
            if (volumeMultiplier == 0){
                volumeMultiplier = 0.001;
                break;
            }
            volumeMultiplier *= silenceCallback.applyAsDouble(currentSPL * volumeMultiplier);
        }
        gainProcessor.setGain(volumeMultiplier);
        gainProcessor.process(audioEvent);
        return true;
    }


    @Override
    public void processingFinished() {
    }
}
