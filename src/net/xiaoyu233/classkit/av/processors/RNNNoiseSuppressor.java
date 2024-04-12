package net.xiaoyu233.classkit.av.processors;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import com.sun.jna.Pointer;
import de.maxhenkel.rnnoise4j.RNNoise;

public class RNNNoiseSuppressor implements AudioProcessor {
    private final Pointer context = RNNoise.INSTANCE.rnnoise_create(null);
    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] input = audioEvent.getFloatBuffer();
        if (input.length == 0 ){
            return true;
        }
        float[] out = new float[input.length];
        RNNoise.INSTANCE.rnnoise_process_frame(context, input, out);
        audioEvent.setFloatBuffer(out);
        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        RNNoise.INSTANCE.rnnoise_destroy(context);
    }

    @Override
    public void processingFinished() {

    }
}
