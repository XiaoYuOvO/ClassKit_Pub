package net.xiaoyu233.classkit.event;

public class SilenceEvent extends ClassEvent{
    private final boolean silence;
    public SilenceEvent(boolean silence) {
        super(EventType.SILENCE);
        this.silence = silence;
    }

    public boolean isSilence() {
        return silence;
    }

    @Override
    public String toString() {
        return "SilenceEvent{" +
                "silence=" + silence +
                '}';
    }
}
