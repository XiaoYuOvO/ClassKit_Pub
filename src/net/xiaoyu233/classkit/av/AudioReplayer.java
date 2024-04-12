package net.xiaoyu233.classkit.av;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import net.xiaoyu233.classkit.av.processors.*;
import net.xiaoyu233.classkit.util.Utils;
import net.xiaoyu233.classkit.util.profiler.ProfileType;
import org.bytedeco.javacv.FrameRecorder;

import javax.sound.sampled.*;
import javax.xml.transform.Source;
import java.nio.ShortBuffer;
import java.util.function.Consumer;

import static net.xiaoyu233.classkit.util.Utils.intel;

public class AudioReplayer implements AudioListener{
    private final SourceDataLine dataLine;
    private AudioPlayer currentPlayer;
    private BooleanControl muteControl;
    private final AudioEvent audioEvent;
    private final AudioManager manager = new AudioManager();
    private final GainProcessor gainProcessor;
    private final SilenceDetector silenceDetector = new SilenceDetector(-10);
    private double targetSPL = 35D;
    private final SoundLevelNormalizer soundLevelNormalizer = new SoundLevelNormalizer(this.targetSPL);
    private volatile boolean hasTerminated;
    //    private final FloatControl gainControl;
    private float forceGain = 1f;

    public AudioReplayer(SourceDataLine dataLine) throws LineUnavailableException {
        System.out.println("Opening audio output at " + intel.getMixerInfo().getName());
        this.dataLine = dataLine;
        TarsosDSPAudioFormat format = JVMAudioInputStream.toTarsosDSPFormat(dataLine.getFormat());
        audioEvent = new AudioEvent(format);
        this.gainProcessor = new GainProcessor(forceGain);
//        this.manager.addProcessor(new RNNNoiseSuppressor());
        VolumeProcessor volumeProcessor = new VolumeProcessor();
        volumeProcessor.addListener(new VolumeDetector());
        volumeProcessor.addListener(silenceDetector);
        volumeProcessor.addListener(soundLevelNormalizer);
        this.manager.addProcessor(volumeProcessor);
        this.manager.addProcessor(gainProcessor);
        reopenPlaybackChannel();
    }

    public void addSilenceCallback(Consumer<Boolean> callback){
        this.silenceDetector.addSilenceCallback(callback);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    public void setForceVolumeGain(float gain){
        this.gainProcessor.setGain(gain);
//        gainControl.setValue(Math.max(Math.min(gain,gainControl.getMaximum()),gainControl.getMinimum()));
        this.forceGain = gain;
    }

    public void reopenPlaybackChannel() {
        if (currentPlayer != null){
            this.manager.removeProcessor(this.currentPlayer);
        }
        muteControl = ((BooleanControl) dataLine.getControl(BooleanControl.Type.MUTE));
        this.currentPlayer = new AudioPlayer(this.dataLine);
        System.out.println("Play back open at " + dataLine.getLineInfo());
        this.manager.addProcessor(this.currentPlayer);
    }

    public void setTargetSPL(double targetSPL) {
        this.soundLevelNormalizer.setTargetSPL(targetSPL);
        this.targetSPL = targetSPL;
    }

    public double getTargetSPL() {
        return targetSPL;
    }

    public double getSilenceThreshold() {
        return silenceDetector.getThreshold();
    }

    public void setSilenceThreshold(double threshold){
        this.silenceDetector.setThreshold(threshold);
    }

    public double getSplitThreshold() {
        return soundLevelNormalizer.getSplitThreshold();
    }

    public void setSplitThreshold(double threshold){
        this.soundLevelNormalizer.setSplitThreshold(threshold);
    }

    public void enableNormalize(){
        this.soundLevelNormalizer.setEnabled(true);
    }

    public void disableNormalize() {
        this.soundLevelNormalizer.setEnabled(false);
    }

    public float getForceVolumeGain(){
        return this.forceGain;
    }

    public void setMute(boolean mute){
        this.muteControl.setValue(mute);
    }

    @Override
    public void onSample(AudioSample sample) {
        long start = System.currentTimeMillis();
//        new EqualizerBand()
        audioEvent.setFloatBuffer(shortToFloat(sample.getData().duplicate()));
//        byte[] b2 = new byte[b.length];
//        this.converter.toByteArray(audioEvent.getFloatBuffer(),b2);
        this.manager.process(audioEvent);
//        byte[] bytes = floatToByte(audioEvent.getFloatBuffer());
//        this.dataLine.write(bytes ,0,bytes.length);
        ProfileType.AUDIO_PLAYBACK_TIME.updateValue(System.currentTimeMillis() - start);
//        System.out.println("Replay took " + (System.currentTimeMillis() - start));
    }

    public void addProcessor(AudioProcessor processor){
        this.manager.addProcessor(processor);
    }

    @Override
    public void streamStart() {
        dataLine.start();
    }

    @Override
    public void onTerminated() {
        this.hasTerminated = true;
    }

    @Override
    public String getName() {
        return "audio_replayer";
    }

    @Override
    public void requestStop() throws FrameRecorder.Exception {
        dataLine.close();
    }

    @Override
    public boolean isStarted() {
        return dataLine.isOpen();
    }

    @Override
    public boolean isStopped() {
        return !dataLine.isOpen();
    }

    @Override
    public void onStopped() {

    }

    public float[] shortToFloat(ShortBuffer data) {
        int i = 0;
        float[] dData = new float[data.array().length];

        while (data.remaining() > 0) {
            short s = data.get();
            dData[i] = (float) s / 32767f; // real
            ++i;
        }
        return dData;
    }

}
