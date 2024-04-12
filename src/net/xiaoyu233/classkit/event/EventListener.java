package net.xiaoyu233.classkit.event;

public interface EventListener<E extends ClassEvent> {
    void onEvent(E event);
    default void onEventRaw(ClassEvent event){
        this.onEvent((E) event);
    }
}
