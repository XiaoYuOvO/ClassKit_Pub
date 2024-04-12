package net.xiaoyu233.classkit.event;

public abstract class ClassEvent {
    private final EventType<?> type;

    protected ClassEvent(EventType<?> type) {
        this.type = type;
    }

    public EventType<?> getType() {
        return type;
    }
}
