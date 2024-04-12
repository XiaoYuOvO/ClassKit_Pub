package net.xiaoyu233.classkit.event;

public class EventType<E extends ClassEvent> {
    public static final EventType<?> CLASS_BEGIN = new EventType<>(ClassBeginEvent.class);
    public static final EventType<SilenceEvent> SILENCE = new EventType<>(SilenceEvent.class);
    public static final EventType<ClassOverEvent> CLASS_OVER = new EventType<>(ClassOverEvent.class);
    private final Class<E> eventClass;

    public EventType(Class<E> eventClass) {
        this.eventClass = eventClass;
    }

//    public E create(){
//        return this.factory.create();
//    }

    private interface EventFactory<E>{
        E create();
    }
}
