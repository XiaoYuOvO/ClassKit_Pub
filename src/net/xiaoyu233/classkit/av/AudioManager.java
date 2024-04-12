package net.xiaoyu233.classkit.av;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

import java.util.ArrayList;
import java.util.List;

public class AudioManager {
    private final List<AudioProcessor> processors = new ArrayList<>();
    public void addProcessor(AudioProcessor processor) {
        this.processors.add(processor);
    }

    public void process(AudioEvent event){
        for (final AudioProcessor processor : processors) {
            if(!processor.process(event)){
                //skip to the next audio processors if false is returned.
                break;
            }
        }
    }

    public void removeProcessor(AudioProcessor processor){
        this.processors.remove(processor);
    }
}
