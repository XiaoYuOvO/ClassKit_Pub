package net.xiaoyu233.classkit.managment;

import com.google.common.collect.Lists;
import net.xiaoyu233.classkit.event.ClassEvent;
import net.xiaoyu233.classkit.event.EventListener;
import net.xiaoyu233.classkit.event.EventType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<EventType<?>, List<EventListener<?>>> listeners = new HashMap<>();

    public <E extends ClassEvent> void registerListener(EventType<E> type, EventListener<E> listener){
        listeners.computeIfAbsent(type, (e) -> Lists.newArrayList()).add(listener);
    }

    public void onEvent(ClassEvent event){
        EventType<?> type = event.getType();
        List<EventListener<?>> eventListeners = this.listeners.get(type);
        if (eventListeners != null){
            eventListeners.forEach((eventListener -> eventListener.onEventRaw(event)));
        }
    }

    public void sendEvent(@Nonnull ClassEvent event) {
//        System.out.println("On class event send:" + event);
        List<EventListener<?>> eventListeners = listeners.get(event.getType());
        if (eventListeners != null){
            for (EventListener<?> eventListener : eventListeners) {
                eventListener.onEventRaw(event);
            }
        }
    }
}
